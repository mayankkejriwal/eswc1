package goldStandard;
import java.io.*;
import java.util.*;


import general.CSVParser;


public class postProcessScores {

	static class Dedup{
	public static void isolateZerosFile(String input, String output)throws IOException{
		Scanner in=new Scanner(new FileReader(new File(input)));
		PrintWriter out=new PrintWriter(new File(output));
		
		while(in.hasNextLine()){
			String[] line=in.nextLine().split("\t");
			if(line[1].equals("0.0"))
				out.println(line[0]);
		}
		
		
		in.close();
		out.close();
	}
	
	// /must be included
	public static void postProcessMultipleScores(String input, String outputfolder)throws IOException{
		Scanner in=new Scanner(new FileReader(new File(input)));
		HashMap<String,HashMap<Double,HashSet<String>>> scores=new HashMap<String,HashMap<Double,HashSet<String>>>();
		
		while(in.hasNextLine()){
			String[] line=in.nextLine().split("\t");
			String[] l2=line[1].split(" ");
			if(!scores.containsKey(l2[1]))
				scores.put(l2[1], new HashMap<Double,HashSet<String>>());
			
			HashMap<Double,HashSet<String>> scores1=scores.get(l2[1]);
			
			if(!scores1.containsKey(Double.parseDouble(l2[0])))
				scores1.put(Double.parseDouble(l2[0]), new HashSet<String>());
			String[] q=line[0].split(" ");
			
			String trial=q[1]+" "+q[0];
			if(scores1.get(Double.parseDouble(l2[0])).contains(trial))
			continue;
			else
			scores1.get(Double.parseDouble(l2[0])).add(line[0]);
		}
		
		
		in.close();
		
		for(String a:scores.keySet()){
			PrintWriter out=new PrintWriter(new File(outputfolder+a));
			ArrayList<Double> doub=new ArrayList<Double>(scores.get(a).keySet());
		Collections.sort(doub);
		
		
		for(int i=doub.size()-1; i>=0; i--){
			for(String t:scores.get(a).get(doub.get(i)))
				out.println(t+"\t"+doub.get(i));
		}
		out.close();
		}
		
	}
	
	public static void searchInSortedFile(String sortedScores, int index1, int index2)throws IOException{
		Scanner in=new Scanner(new FileReader(new File(sortedScores)));
		String l1=index1+" "+index2+"\t";
		String l2=index2+" "+index1+"\t";
		int count=0;
		boolean found=false;
		while(in.hasNextLine()){
			String line=in.nextLine();
			if(line.contains(l1)||line.contains(l2)){
				System.out.println(count);
				found=true;
				break;
			}
			count++;
			
		}
		if(!found)
			System.out.println("not found");
		in.close();
	}
	
	public static void test(String records)throws IOException{
		Scanner in=new Scanner(new FileReader(records));
		while(in.hasNextLine()){
			String line=in.nextLine();
			String[] p=new CSVParser().parseLine(line);
			for(String p1:p)
				System.out.println(p1);
			System.exit(-1);
		}
		
		in.close();
	}
	}
	
	public static void main(String[] args)throws IOException{
		
		
		//postProcessMultipleScoresRL("/host/hadoop_files/libraries_output","/host/heteroDatasets/icde_experiments/libraries/sortedScores/");
		//searchInSortedFile("/host/hadoop_files/sortedScores",0,1);
		//isolateZerosFile("/host/heteroDatasets/cikm_experiments/cora/sortedScores/TF","/host/heteroDatasets/cikm_experiments/cora/sortedScores/TF0");
		
		String prefix="/host/heteroDatasets/eswc1/Restaurants/";
		
		
		//follow this sequence:
		/*
		processBigTF.revLineOrder(prefix+"TF_output", prefix+"TF_output1");
		processBigTF.pruneLower(prefix+"TF_output1", prefix+"TF_output1_0.01",0.01);
		processBigTF.convertToOriginal(prefix+"TF_output1_0.01", prefix+"TF_0.01");
		RL.postProcessMultipleScoresRL(prefix+"TF_0.01", prefix+"sortedScores/");
		*/
		
	//	RL.precRecPointsRL(prefix+"sortedScores/J",prefix+"goldStandard",prefix+"sortedScores/precRec_J");
			processClassifier(prefix+"classifierOutput",prefix+"goldStandard",
				prefix+"sortedScores/classifier",prefix+"sortedScores/precRec_classifier"); 
				
	}
	
	/*
	 * Recall classifierOutput is of format 1 2 0.99 0.01 (where 1 and 2 refer to indices from the two files)
	 * This code will first SORT and print outputTrunc as: 1 2 0.99 etc.
	 * It will then call RL.precRec... on the trunc file. Thereon, the mechanisms are the same as
	 * for the score files 
	 */
	public static void processClassifier(String svmOutput, String goldStandard, String outputTrunc, String outputPrecRec)throws IOException{
		HashMap<Double,HashSet<String>> scores=new HashMap<Double,HashSet<String>>();
		Scanner in=new Scanner(new FileReader(svmOutput));
		while(in.hasNextLine()){
			String[] line=in.nextLine().split("\t");
			double score=Double.parseDouble(line[1].split(" ")[0]);
			if(!scores.containsKey(score))
				scores.put(score,new HashSet<String>());
			scores.get(score).add(line[0]);
		}
		in.close();
		ArrayList<Double> keys=new ArrayList<Double>(scores.keySet());
		Collections.sort(keys);
		PrintWriter out=new PrintWriter(new File(outputTrunc));
		for(int i=keys.size()-1; i>=0; i--){
			double key=keys.get(i);
			for(String a: scores.get(key))
				out.println(a+"\t"+key);
		}
		out.close();
		
		
		RL.precRecPointsRL(outputTrunc,goldStandard,outputPrecRec);
	}
	
	static class processBigTF{
		
		public static void pruneLowerUpper(String input, String output,double lt, double ut)throws IOException{
			Scanner in=new Scanner(new FileReader(input));
			PrintWriter out=new PrintWriter(new File(output));
			double a=0.0;
			while(in.hasNextLine()){
				String line=in.nextLine();
				String score=line.split("\t")[0];
				a=Double.parseDouble(score);
				if(a>lt && a<=ut)
					out.println(line);
			}
			out.close();
			in.close();
		}
		
		public static void pruneLower(String input, String output,double lt)throws IOException{
			Scanner in=new Scanner(new FileReader(input));
			PrintWriter out=new PrintWriter(new File(output));
			while(in.hasNextLine()){
				String line=in.nextLine();
				String score=line.split("\t")[0];
				if(Double.parseDouble(score)>lt)
					out.println(line);
			}
			out.close();
			in.close();
		}
		
		public static void convertToOriginal(String input, String output)throws IOException{
			Scanner in=new Scanner(new FileReader(input));
			PrintWriter out=new PrintWriter(new File(output));
			while(in.hasNextLine()){
				String[] line=in.nextLine().split("\t");
				out.println(line[1]+"\t"+line[0]+" TF");
			}
			out.close();
			in.close();
		}
		
		public static void revLineOrder(String input, String output)throws IOException{
			Scanner in=new Scanner(new FileReader(input));
			PrintWriter out=new PrintWriter(new File(output));
			while(in.hasNextLine()){
				String[] line=in.nextLine().split("\t");
				String score=line[1].split(" ")[0];
				out.println(score+"\t"+line[0]);
				
			}
			out.close();
			in.close();
		}
		
		public static void combine(String input1, String input2, String output)throws IOException{
			Scanner in=new Scanner(new FileReader(input1));
			HashSet<String> arr=new HashSet<String>();
			while(in.hasNextLine())
				arr.add(in.nextLine());
			in.close();
			in=new Scanner(new FileReader(input2));
			while(in.hasNextLine())
				arr.add(in.nextLine());
			in.close();
			PrintWriter out=new PrintWriter(new File(output));
			for(String a:arr)
				out.println(a);
			out.close();
		}
		
		public static void splitFile(String input, String outputPrefix, int num, int total)throws IOException{
			int[] splits=new int[num+1];
			splits[0]=0;
			splits[num]=total;
			PrintWriter[] out=new PrintWriter[num];
			for(int i=1; i<num; i++){
				splits[i]=(total/num)*i;
				out[i-1]=new PrintWriter(new File(outputPrefix+Integer.toString(i)));
			
			}
			out[num-1]=new PrintWriter(new File(outputPrefix+Integer.toString(num)));
			Scanner in=new Scanner(new FileReader(input));
			
			for(int i=0; i<splits.length-1; i++){
				HashSet<String> arr=new HashSet<String>();
				int lim=splits[i+1]-splits[i];
				int count=0;
				while(in.hasNextLine()&&count<=lim){
					arr.add(in.nextLine());
					count++;
				}
				for(String a:arr)
					out[i].println(a);
				out[i].close();
			}
			
			
			in.close();
			
		}
		
		
		public static void countLines(String input)throws IOException{
			Scanner in=new Scanner(new FileReader(input));
			int count=0;
			while(in.hasNextLine()){
				in.nextLine();
				count++;
			}
		
			in.close();
			System.out.println(count);
		}
		
		public static void removeDups(String input, String output)throws IOException{
			Scanner in=new Scanner(new FileReader(input));
			HashSet<String> p=new HashSet<String>(50000);
			while(in.hasNextLine()){
				p.add(in.nextLine());
			}
			in.close();
			PrintWriter out=new PrintWriter(new File(output));
			for(String a:p)
				out.println(a);
			out.close();
		}
		
		public static void printFirstNLines(String file, int n)throws IOException{
			int count=0;
			Scanner in=new Scanner(new FileReader(file));
			while(in.hasNextLine()&&count<n){
				System.out.println(in.nextLine());
				count++;
			}
		}

		public static void precRecPointsRL(String sortedScores, String gold, String output)throws IOException{
			
			//ArrayList<String> actual=new ArrayList<String>();
			HashSet<String> gs=new HashSet<String>();
			Scanner g=new Scanner(new FileReader(gold));
			while(g.hasNextLine())
				gs.add(g.nextLine());
			
			g.close();
			int size=gs.size();
			Scanner in=new Scanner(new FileReader(sortedScores));
			PrintWriter out=new PrintWriter(new File(output));
			int count=0;
			int total=1;
			double prec=0.0;
			double rec=0.0;
			while(in.hasNextLine()){
				String line=(in.nextLine().split("\t")[1]);
				if(gs.contains(line))
					count++;
				prec=count*1.0/total;
				rec=count*1.0/size;
				total++;
				out.println(prec+","+rec);
			}
			
			in.close();
			out.close();
			
			
			
		}
	}

	static class RL{
	public static void precRecPointsRL(String sortedScores, String gold, String output)throws IOException{
		
		ArrayList<String> actual=new ArrayList<String>();
		HashSet<String> gs=new HashSet<String>();
		Scanner g=new Scanner(new FileReader(gold));
		while(g.hasNextLine())
			gs.add(g.nextLine());
		
		g.close();
		
		Scanner in=new Scanner(new FileReader(sortedScores));
		while(in.hasNextLine()){
			actual.add(in.nextLine().split("\t")[0]);
		}
		
		in.close();
		ArrayList<Integer> cumCorrect=new ArrayList<Integer>();
		
		int count=0;
		for(int i=0; i<actual.size(); i++){
			//String[] index=actual.get(i).split(" ");
		//	int index1=Integer.parseInt(index[1]);
			//int index0=Integer.parseInt(index[0]);
			
			
			if(gs.contains(actual.get(i)))
				count++;
			cumCorrect.add(count);
		}
		//System.out.println(cumCorrect);
		ArrayList<Double> prec=new ArrayList<Double>();
		ArrayList<Double> rec=new ArrayList<Double>();
		for(int i=0; i<cumCorrect.size(); i++){
			prec.add(cumCorrect.get(i)*1.0/(i+1));
			rec.add(cumCorrect.get(i)*1.0/(gs.size()));
		}
		
		
		PrintWriter out=new PrintWriter(new File(output));
		for(int i=0; i<prec.size(); i++)
			out.println(prec.get(i)+","+rec.get(i));
		out.close();
		
	}
	}
}
