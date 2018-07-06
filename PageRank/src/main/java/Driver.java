
public class Driver {

    public static void main(String[] args) throws Exception {

        UnitMultiplication multiplication = new UnitMultiplication();
        UnitSum sum = new UnitSum();

        //args0: dir of transition.txt
        //args1: dir of PageRank.txt
        //args2: dir of unitMultiplication result result = subPR
        //args3: times of convergence
        String transitionMatrix = args[0];
        String prMatrix = args[1];
        String subPR = args[2];
        int count = Integer.parseInt(args[3]);
        for(int i=0;  i<count;  i++) { // pr1, pr2, pr3, ... -> / subpr0, subpr1, subpr2...
            String[] multiplicationArgs = {transitionMatrix, prMatrix+i, subPR+i};
            multiplication.main(multiplicationArgs);
            String[] sumArgs = {subPR+i, prMatrix+(i+1)};
            sum.main(sumArgs);
        }
    }
}
