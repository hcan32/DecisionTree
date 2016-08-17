
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Huseyincan Sahin
 */
public class DecisionTree {

    public static int rootIndex = 0 ;
    
    
    // prediction function
    // takes the tree as string and predicts the testData
    // example usage can be seen in main function
    public static void predictLabel(String splitRoot[],ArrayList<String> att,ArrayList<String> testData,int labelIndex){
        
         for(int i=0;i<splitRoot.length;i++){
             String rootLeaves[] = splitRoot[i].split("\\s+");
             int predicter = 0 , j = 2 ;
             if( rootLeaves.length > 1 && rootLeaves[1].compareTo(testData.get(0)) == 0 ){
                 if( rootLeaves.length == 3 && rootLeaves[2].contains(":")){
                         System.out.println("Predict : " + rootLeaves[j] + " Real : " + testData.get(labelIndex));                    
                 }else{
                    predicter++;
                    while( j < rootLeaves.length ){
                        int idx = att.indexOf(rootLeaves[j]);
                        j++;
                        if( idx != -1 && testData.get(idx).compareTo(rootLeaves[j]) == 0 ){
                            predicter++;
                            j++;
                        }

                        if( rootLeaves[j].contains(":") && predicter > 1 ){
                            System.out.println("Predict : " + rootLeaves[j]+ " Real : " + testData.get(labelIndex));
                            j = rootLeaves.length;
                        }
                    }
                 }
             }              
            }
    }
    
    
    
    
    public static String getLabelConditition(ArrayList<ArrayList<String>> allTable,int labelIndex,ArrayList<String> lbl){
        int[] counts = new int[lbl.size()];
        for(int i=0 ; i<allTable.size() ; i++){
            for( int j=0;j<lbl.size();j++){
                if(allTable.get(i).get(labelIndex).equals(lbl.get(j)))
                    counts[j] = counts[j] + 1;
            }
        }
        int max = 0 , maxIndex = 0 ;
        for(int i=0 ; i< lbl.size() ; i++){
            if(counts[i] > max){
                max = counts[i];
                maxIndex = i ;
            }
        }
        return lbl.get(maxIndex);
    }
    
    // generates table with given condition in column index of partition
    public static ArrayList<ArrayList<String>> generateTable(ArrayList<ArrayList<String>> allTable,int partition,String condition){
        
        ArrayList<ArrayList<String>> generated = new ArrayList<ArrayList<String>>();
        for(int i=0;i<allTable.size();i++){
            if( allTable.get(i).get(partition).equals(condition) ){
                generated.add(allTable.get(i));
            }
        }
        return generated;
    }
 
    // generated tree as string
    // recursive function
    // uses calculateGain function to get max gain 
    // and split the table through that index recursively
    public static String generateTree(ArrayList<ArrayList<String>> allTable,int labelIndex,
            ArrayList<ArrayList<String>> attributes,
            ArrayList<Integer> forbiddenValues,int count,
            ArrayList<String> attributeNames)
    {
        String st = "";

        double maxGain = 0 ;
        int maxGainIndex = 0;
        for(int i=0 ;i<labelIndex;i++){
            double currentGain = calculateGain(labelIndex,i,allTable,attributes,forbiddenValues,attributeNames);
            if(currentGain > maxGain){
                maxGain = currentGain ;
                maxGainIndex = i;
            }
        }
        forbiddenValues.add(maxGainIndex);

        if(maxGain < 0.00001){
            st += "\n";
            for(int i=0 ; i<count+1;i++){
                st += "    ";
            }
            st += getLabelConditition(allTable,labelIndex,attributes.get(labelIndex))  + ":" + "\n";// "(calculated gain : " + maxGain + ")\n";
            
            return st;
        }
        else{

            for( int i=0 ; i<attributes.get(maxGainIndex).size();i++){

                st += "\n" ;
                for(int j=0 ; j<count+1;j++){
                    st +=  "    ";
                }
                if(count == -1)
                    rootIndex = maxGainIndex;
                st += attributeNames.get(maxGainIndex) + "\n";// "(calculated gain : " + maxGain + ")\n";
                for(int j=0 ; j<count+1;j++){
                    st +=  "    ";
                }               
                ArrayList<ArrayList<String>> generalTable1 = generateTable(allTable, maxGainIndex, attributes.get(maxGainIndex).get(i));
                st += attributes.get(maxGainIndex).get(i);
                st += generateTree(generalTable1, labelIndex, attributes,(ArrayList<Integer>) forbiddenValues.clone(), count+1,attributeNames);                 
            }
            
        }
        return st ;

    }
    
    
    // calculation info
    public static double calculateInfo(int values[],int tableSize){
        double info = 0;
        for(int i=0;i<values.length;i++){
            if(values[i]!=0)
                info += (values[i] /(double)tableSize) * ( Math.log( values[i] /(double)tableSize) / Math.log(2));
        }
        info = -info;
        return info ;     
    }
    
    
    // calculation gain of the index as gainIndex 
    // tree uses this function to determine which index has max gain
    public static double calculateGain(int labelIndex,int gainIndex,
            ArrayList<ArrayList<String>> table,ArrayList<ArrayList<String>> att,
            ArrayList<Integer> forbidden,ArrayList<String> attributeNames){
        if(forbidden.contains(gainIndex))
            return 0;
        int[] labelCounts = new int[att.get(labelIndex).size()]; 
        int[] infoCounts = new int[att.get(gainIndex).size()]; 
        int[] infoPositives = new int[att.get(gainIndex).size()];
        
        int[][] infoStorage = new int[att.get(labelIndex).size()][att.get(gainIndex).size()];
      
        for (int[] row : infoStorage)
            Arrays.fill(row, 0);

        for( int i=0 ; i<table.size() ; i++ ){
                for(int j=0 ; j<att.get(labelIndex).size() ; j++){
                    if( att.get(labelIndex).get(j).equals(table.get(i).get(labelIndex)) )
                        labelCounts[j] = labelCounts[j] + 1;
                }
                for(int j=0 ; j<att.get(gainIndex).size() ; j++){
                    if( att.get(gainIndex).get(j).equals(table.get(i).get(gainIndex)) ){    
                        
                        if( table.get(i).get(labelIndex).equals(att.get(labelIndex).get(1))  )
                            infoPositives[j] = infoPositives[j] + 1;
                        
                        for( int index=0 ; index<att.get(labelIndex).size() ; index++){
                            if( table.get(i).get(labelIndex).equals(att.get(labelIndex).get(index))  ){
                                infoStorage[index][j] = infoStorage[index][j] + 1;
                            }

                        }
                        infoCounts[j] = infoCounts[j] + 1;
                    }
                }
            
        }

        // get expected info
        double info = calculateInfo(labelCounts,table.size());
        
        // getting needed info
        double infoNeeded = 0 ;
        for(int i=0;i<infoCounts.length;i++){
            //int[] infos = {infoPositives[i],infoCounts[i]-infoPositives[i]};
            
            int[] infos = new int[att.get(labelIndex).size()];
            for(int j=0 ; j<att.get(labelIndex).size() ; j++){
                infos[j] = infoStorage[j][i]; 
            }
            infoNeeded += (infoCounts[i] /(double)table.size())  * calculateInfo(infos,infoCounts[i]);
            //System.out.println(attributeNames.get(gainIndex) + " " + att.get(gainIndex).get(i) + " = " + Arrays.toString(infos) + " I = " + calculateInfo(infos,infoCounts[i]) );
        }
        
        info = info - infoNeeded;
        //System.out.print("Gain for " + attributeNames.get(gainIndex) + " : "  + info +"\n");
        return info;

    }
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String line = "";			
        String breaker = ";";		// breaker string for split the lines
        int labelIndex = 4;			// labelIndex , the first one would be zero
        
        // reading stuff
        BufferedReader reader = new BufferedReader(new FileReader("data11.csv"));
        line = reader.readLine();
        int attributeNum = line.split(breaker).length;
        ArrayList<String> attributeNames = new ArrayList<String>();
        ArrayList<Integer> forbiddenValues = new ArrayList<Integer>();
        ArrayList<ArrayList<String>> allTable = new ArrayList<ArrayList<String>>(attributeNum);
        ArrayList<ArrayList<String>> attributes = new ArrayList<ArrayList<String>>(attributeNum);
        
        // assign attribute names
        for(int i=0 ; i< attributeNum; i++){
                attributeNames.add(line.split(breaker)[i]);
        }
        while ( (line = reader.readLine() ) != null ){

            String[] tuple = line.split(breaker);
            ArrayList<String> currentTuple = new ArrayList<String>();
            for(int i=0; i< tuple.length ; i++){
                currentTuple.add(tuple[i]);
            } 
            allTable.add(currentTuple);
        }
        
        // adding first attribute values
        for(int i=0 ; i < attributeNum ; i++){
            ArrayList<String> att = new ArrayList<String>();
            att.add( allTable.get(0).get(i) );
            attributes.add(att);
        }
        // adding other attribute values but only unique ones
        // so same value cant be seen twice
        for(int i=0 ; i < attributeNum ; i++){
            for(int j =0 ; j< allTable.size() ; j++){
                if( attributes.get(i).contains(allTable.get(j).get(i)) == false ){
                    attributes.get(i).add(allTable.get(j).get(i));
                }
            }
        }

        // close reader
        reader.close();
         
        // generate tree function
        // allTable => all lines
        // labelIndex => index number of the label column
        // attributes => unique attributes
        // forbiddenValues => empty at start , grows with recursion
        // attributeNames => column names
        String tree = generateTree( allTable,labelIndex,
            attributes,
            forbiddenValues,-1,attributeNames);
         
         // print tree as string
         System.out.println("\n\n\n***** TREE *****\n\n\n");
         System.out.println(tree);
         System.out.println("\nEND");
    }
    
}
