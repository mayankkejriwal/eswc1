package generateSupFiles;



import java.io.*;
import java.util.*;

public class GenerateSVMSupFiles {

	
	static String prefix="/host/heteroDatasets/eswc1/Restaurants/";
	static int percent=2;
	static String scoredFile=prefix+"sortedScores/classifier";
	static String gold=prefix+"goldStandard";
	static String output=prefix+"svmSup"+percent;
	static String outputND=prefix+"svmSupNonDup"+percent;
	
	static int num=89;
	static double thresh=0.99;	//number of samples to extract as duplicates from svm file
	
	
	public static void main(String[] args)throws IOException{
		generateIterFilesNum();
		//generateSVMSupFiles();
	}
	
	public static void generateIterFiles()throws IOException{
		Scanner in=new Scanner(new FileReader(scoredFile));
		PrintWriter out=new PrintWriter(new File(output));
		HashSet<String> chosen=new HashSet<String>();
		
		while(in.hasNextLine()){
			String[] line=in.nextLine().split("\t");
			String pair=line[0];
			double score=Double.parseDouble(line[1]);
			if(score>=thresh){
				out.println(pair);
				chosen.add(pair);
			}
			else
				break;
			
		}
		in.close();
		out.close();
		
		Random k=new Random(System.currentTimeMillis()*3);
		ArrayList<String> o=new ArrayList<String>(chosen);
		HashSet<String> chosenND=new HashSet<String>();
		int num=chosen.size();
		int count=0;
		while(count<num){
			int m=k.nextInt(o.size());
			int n=k.nextInt(o.size());
			String c=o.get(m).split(" ")[0]+" "+o.get(n).split(" ")[1];
			if(chosen.contains(c))
				continue;
			else{
				chosenND.add(c);
				count++;
			}
		}
		
				
		out=new PrintWriter(new File(outputND));
		for(String line:chosenND)
			out.println(line);
		out.close();
	}
	
	public static void generateSVMSupFiles()throws IOException{
		Scanner in=new Scanner(new FileReader(gold));
		ArrayList<String> g=new ArrayList<String>();
		ArrayList<Integer> a=new ArrayList<Integer>();
		ArrayList<Integer> b=new ArrayList<Integer>();
		while(in.hasNextLine()){
			String line=in.nextLine();
			g.add(line);
		}
		in.close();
		Random k=new Random(System.currentTimeMillis()*3);
		
		HashSet<String> dups=new HashSet<String>();
		double count=0;
		while(count<(percent/100.0)*g.size()){
			String t=g.get(k.nextInt(g.size()));
			dups.add(t);
			count++;
		}
		
		PrintWriter out=new PrintWriter(new File(output));
		for(String line:dups){
			out.println(line);
			a.add(Integer.parseInt(line.split(" ")[0]));
			b.add(Integer.parseInt(line.split(" ")[1]));
		}
		out.close();
		count=0;
		k=new Random(System.currentTimeMillis()*3);
		HashSet<String> chosenND=new HashSet<String>();
		while(count<dups.size()){
			int m=k.nextInt(a.size());
			int n=k.nextInt(b.size());
			String c=a.get(m)+" "+b.get(n);
			if(dups.contains(c))
				continue;
			else{
				chosenND.add(c);
				count++;
			}
		}
		
		out=new PrintWriter(new File(outputND));
		for(String line:chosenND)
			out.println(line);
		out.close();
		
	}

	public static void generateIterFilesNum()throws IOException{
		Scanner in=new Scanner(new FileReader(scoredFile));
		PrintWriter out=new PrintWriter(new File(output));
		HashSet<String> chosen=new HashSet<String>();
		int count=0;
		while(in.hasNextLine() && count<num){
			String[] line=in.nextLine().split("\t");
			String pair=line[0];
			//double score=Double.parseDouble(line[1]);
			
				out.println(pair);
				chosen.add(pair);
				count++;
			
			
		}
		in.close();
		out.close();
		
		Random k=new Random(System.currentTimeMillis()*3);
		ArrayList<String> o=new ArrayList<String>(chosen);
		HashSet<String> chosenND=new HashSet<String>();
		
		count=0;
		while(count<num){
			int m=k.nextInt(o.size());
			int n=k.nextInt(o.size());
			String c=o.get(m).split(" ")[0]+" "+o.get(n).split(" ")[1];
			if(chosen.contains(c))
				continue;
			else{
				chosenND.add(c);
				count++;
			}
		}
		
				
		out=new PrintWriter(new File(outputND));
		for(String line:chosenND)
			out.println(line);
		out.close();
	}
}
