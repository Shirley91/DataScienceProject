import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnitMultiplication {

    public static class TransitionMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            // transition mapper
            //input format: fromPage\t toPage1,toPage2,toPage3
            //target: build transition matrix unit -> fromPage\t toPage=probability
            //output: value = toPage=prob
            String[] fromTos = value.toString().trim().split("\t");
            // edge case: no toPage
            if (fromTos.length < 2){
                return;
            }
            String[] tos = fromTos[1].split(",");
            String outputKey = fromTos[0];
            for (String to: tos){
                context.write(new Text(outputKey), new Text(to + "=" + (double)1/tos.length));
            }
        }
    }

    public static class PRMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            // PR mapper
            //input format: Page\t PageRank(n)
            //target: write to reducer
            //outputKey = Page
            //outputValue = PR(n)
            String[] idPr = value.toString().trim().split("\t");
            context.write(new Text(idPr[0]), new Text(idPr[1]));
        }
    }

    public static class MultiplicationReducer extends Reducer<Text, Text, Text, Text> {

        // teleporting: solve edge cases
        // PR(n) = (1-beta) * M * PR(n-1) + beta * PR(n-1)
        float beta;

        @Override
        public void setup(Context context) {
            Configuration conf = context.getConfiguration();
            beta = conf.getFloat("beta", 0.2f);
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            //input key = fromPage
            //value=<toPage=probability..., pageRank>, eg value = <2=1/3, 5=1/3, 8=1/3, 1>
            //target: get the unit multiplication (subPR)
            //outputKey = toPage
            //outputValue = prob*PR(n)
            double prCell = 0;
            List<String> transitionCellList = new ArrayList<String>();
            for (Text value: values){
                if (value.toString().contains("=")){
                    transitionCellList.add(value.toString());
                } else {
                    prCell = Double.parseDouble(value.toString());
                }
            }

            for (String transCell: transitionCellList) {
                // transcell: toPage=prob
                String toId = transCell.split("=")[0];
                double prob = Double.parseDouble(transCell.split("=")[1]);
                double subPr = String.valueOf(prob * prCell * (1-beta));
                context.write(new Text(toId), new Text(subPr));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.setFloat("beta", Float.parseFloat(args[3]));
        Job job = Job.getInstance(conf);
        job.setJarByClass(UnitMultiplication.class);

        //how chain two mapper classes?

        job.setReducerClass(MultiplicationReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TransitionMapper.class); // for multi-mapper
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, PRMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);
    }

}
