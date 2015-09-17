package goldStandard;

import java.io.*;
import java.util.*;

import tsg.JaccardFromTF;

public class ProcessPrecRec {

	/*
	 * This class was designed for further processing of precRec.  We will only consider highest precision
	 * at given recall point, sort the file and print it as recPrec.
	 */
	
	static String dataset="Restaurants";
	static String prefix="/host/heteroDatasets/eswc1/"+dataset+"/sortedScores/";
	static String gold="/host/heteroDatasets/eswc1/"+dataset+"/goldStandard";
	static String type="classifier";
	static String unique="J_500";
	
	public static void main(String[] args)throws IOException{
		convertToTrimmedRecPrec();
		//findHighestFScore();
		//evalTFFileTopN(500);
		//printUniqueFile(500);
		//evalUniqueFile();
		
	}
	
	public static void evalUniqueFile()throws IOException{
		Scanner in=new Scanner(new FileReader(gold));
		HashSet<String> g=new HashSet<String>();
		while(in.hasNextLine())
			g.add(in.nextLine());
		in.close();
		
		in=new Scanner(new FileReader(prefix+unique));
		HashSet<String> f=new HashSet<String>();
		while(in.hasNextLine()){
			f.add(in.nextLine().split("\t")[0]);
		}
		in.close();
		
		int total=f.size();
		int actual=total;
		if(g.size()<actual)
			actual=g.size();
		int correct=JaccardFromTF.intersect(g,f).size();
		double recall=1.0*correct/actual;
		double precision=1.0*correct/total;
		double fm=2*recall*precision/(recall+precision);
		System.out.println("Dataset\t Recall\t Precision\t FM");
		System.out.println(dataset+"\t"+recall+"\t"+precision+"\t"+fm);
	}
	
	public static void evalTFFileTopN(int n)throws IOException{
		Scanner in=new Scanner(new FileReader(gold));
		HashSet<String> g=new HashSet<String>();
		while(in.hasNextLine())
			g.add(in.nextLine());
		in.close();
		
		in=new Scanner(new FileReader(prefix+"TF"));
		int count=0;
		HashSet<String> f=new HashSet<String>();
		while(in.hasNextLine()&&count<n){
			f.add(in.nextLine().split("\t")[0]);
			count++;
		}
		in.close();
		
		int total=f.size();
		int actual=total;
		if(g.size()<actual)
			actual=g.size();
		int correct=JaccardFromTF.intersect(g,f).size();
		double recall=1.0*correct/actual;
		double precision=1.0*correct/total;
		double fm=2*recall*precision/(recall+precision);
		System.out.println("Dataset\t Recall\t Precision\t FM");
		System.out.println(dataset+"\t"+recall+"\t"+precision+"\t"+fm);
	}

	//take top n pairs from the file such that no tuple is repeated
	public static void printUniqueFile(int n)throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+type));
		PrintWriter out=new PrintWriter(new File(prefix+unique));
		HashSet<Integer> t1=new HashSet<Integer>();
		HashSet<Integer> t2=new HashSet<Integer>();
		int count=0;
		while(in.hasNextLine()&& count<n){
			String line=in.nextLine();
			int a=Integer.parseInt(line.split("\t")[0].split(" ")[0]);
			if(t1.contains(a))
				continue;
			int b=Integer.parseInt(line.split("\t")[0].split(" ")[1]);
			if(t2.contains(b))
				continue;
			t1.add(a);
			t2.add(b);
			out.println(line);
			count++;
		}
		in.close();
		out.close();
		if(count!=n)
			System.out.println("Early termination! count="+count);
	}

	/*
	 * Reads in the recPrec new files (both TF and J) and prints the highest f-score+which file got it
	 */
	public static void findHighestFScore()throws IOException{
		double maxJ=0;
		Scanner in=new Scanner(new FileReader(prefix+"recPrec_J_new.csv"));
		while(in.hasNextLine()){
			String[] line=in.nextLine().split(",");
			double a=Double.parseDouble(line[0]);
			double b=Double.parseDouble(line[1]);
			if(a+b==0)
				continue;
			double fscore=2*a*b/(a+b);
			if(fscore>maxJ)
				maxJ=fscore;
		}
		in.close();
		
		double maxTF=0;
		in=new Scanner(new FileReader(prefix+"recPrec_TF_new.csv"));
		while(in.hasNextLine()){
			String[] line=in.nextLine().split(",");
			double a=Double.parseDouble(line[0]);
			double b=Double.parseDouble(line[1]);
			if(a+b==0)
				continue;
			double fscore=2*a*b/(a+b);
			if(fscore>maxTF)
				maxTF=fscore;
		}
		in.close();
		
		if(maxJ>=maxTF)
			System.out.println("J \t "+maxJ);
		else
			System.out.println("TF \t "+maxTF);
	}
	
	public static void convertToTrimmedRecPrec()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"precRec_"+type));
		HashMap<Double,Double> recPrec=new HashMap<Double,Double>(20000);
		while(in.hasNextLine()){
			String[] line=in.nextLine().split(",");
			double prec=Double.parseDouble(line[0]);
			double rec=Double.parseDouble(line[1]);
			if(!recPrec.containsKey(rec))
				recPrec.put(rec, prec);
			else
			{
				if(recPrec.get(rec)<prec)
					recPrec.put(rec, prec);
			}
		}
		in.close();
		ArrayList<Double> k=new ArrayList<Double>(recPrec.keySet());
		Collections.sort(k);
		PrintWriter out=new PrintWriter(new File(prefix+"recPrec_"+type+"_new.csv"));
		for(double k1:k)
			out.println(k1+","+recPrec.get(k1));
		out.close();
	}
}
