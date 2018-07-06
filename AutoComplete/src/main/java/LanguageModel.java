import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class LanguageModel {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

		int threashold;

		@Override
		public void setup(Context context) {
			// how to get the threashold parameter from the configuration?
			threashold = context.getConfiguration().getInt("threashold", 10);
		}

		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			if((value == null) || (value.toString().trim()).length() == 0) {
				return;
			}
			// input: I love big\t200
			// cut between I love big
			// write to disk
			//this is cool\t20 '\t' default separate for key and value when write data into HDFS
			String line = value.toString().trim();
			
			String[] wordsPlusCount = line.split("\t");
			if(wordsPlusCount.length < 2) {
				return; // for non-point problem: throw exception('wordsPlusCount < 2')
			}
			
			String[] words = wordsPlusCount[0].split("\\s+");
			int count = Integer.valueOf(wordsPlusCount[1]);

			//how to filter the n-gram lower than threashold
			if (count < threashold){
				return;
			}
			//this is --> cool = 20
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < words.length -1; i++){
				sb.append(words[i] + ' ');
			}

			//what is the outputkey?
			String outputKey = sb.toString().trim();
			//what is the outputvalue?
			String outputValue = words[words.length - 1] + '=' + count;
			//write key-value to reducer?
			context.write(new Text(outputKey), new Text(outputValue));
		}
	}

	public static class Reduce extends Reducer<Text, Text, DBOutputWritable, NullWritable> {

		int topK;
		// get the n parameter from the configuration
		@Override
		public void setup(Context context) {
			topK = context.getConfiguration().getInt("topK", 5);
		}

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			//can you use priorityQueue to rank topN n-gram, then write out to hdfs?
			// collect from mapper
			// topK from value list (treemap method in this part)
			// write to mysql
			// key = I love
			// value list = <big=200, data=20, girl=30, boy=25,...>
			TreeMap<Integer, List<String>> tm = new TreeMap<Integer, List<String>>(Collections.reverseOrder());
			for (Text value: values) {
				String curValue = value.toString().trim();
				// curValue = big=200
				String word = curValue.split("=")[0].trim();
				int count = Integer.parseInt(curValue.split("=")[1].trim());
				if (tm.containsKey(count)){
					tm.get(count).add(word);
				}
				else {
					List<String> list = new ArrayList<String>();
					list.add(word);
					tm.put(count, list);
				}
			}
				Iterator<Integer> iter = tm.keySet().iterator();
			for (int j = 0; iter.hasNext() && j < topK;) {
				int keyCount = iter.next();
				List<String> words = tm.get(keyCount);
				for (String curWord: words){
					// key-value pair, if no value, use NullWritable.get()
					context.write(new DBOutputWritable(key.toString(), curWord, keyCount), NullWritable.get());
					j++;
				}
			}
		}
	}
}
