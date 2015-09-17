package goldStandard;

import java.io.*;
import java.util.*;

import general.CSVParser;

public class GenerateGoldStandards {

	static String prefix="/host/heteroDatasets/eswc1/DBLP-Scholar/";
	public static void main(String[] args)throws IOException{
		generateGoldStandard();
		//printNumTypes(6,6);
	}
	
	//enter the column number index corresponding to 'type' column
	public static void printNumTypes(int type1, int type2)throws IOException{
		HashSet<String> class1=new HashSet<String>();
		HashSet<String> class2=new HashSet<String>();
		
		Scanner in=new Scanner(new FileReader(prefix+"file1.csv"));
		while(in.hasNextLine()){
			class1.add((new CSVParser()).parseLine(in.nextLine())[type1]);
		}
		in.close();
		
		in=new Scanner(new FileReader(prefix+"file2.csv"));
		while(in.hasNextLine()){
			class2.add((new CSVParser()).parseLine(in.nextLine())[type2]);
		}
		in.close();
		
		System.out.println("Number of classes in file1 "+class1.size());
		System.out.println(class1);
		
		System.out.println("Number of classes in file2 "+class2.size());
		System.out.println(class2);
	}
	
	
	public static void verifyLength(int length1, int length2)throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"file1.csv"));
		while(in.hasNextLine()){
			String[] c=(new CSVParser()).parseLine(in.nextLine());
			if(c.length!=length1)
				System.out.println("file1\t"+c.length+"    "+c[0]+"\t"+c[1]);
			
		}
		in.close();
		
		in=new Scanner(new FileReader(prefix+"file2.csv"));
		while(in.hasNextLine()){
			String[] c=(new CSVParser()).parseLine(in.nextLine());
			if(c.length!=length2)
				System.out.println(c.length+"    "+c[0]+"\t"+c[1]);
			
		}
		in.close();
	}
	
	
	public static void generateGoldStandard()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"file1.csv"));
		HashMap<String, Integer> map1=new HashMap<String,Integer>();
		int count=0;
		String csvline=null;
		try{
		while(in.hasNextLine()){
			csvline=in.nextLine();
			String element=(new CSVParser()).parseLine(csvline)[0];
			map1.put(element,count);
			count++;
			
		}
		}
		catch(Exception e){
			System.out.println(csvline);
		}
		in.close();
		
		in=new Scanner(new FileReader(prefix+"file2.csv"));
		HashMap<String, Integer> map2=new HashMap<String,Integer>();
		count=0;
		try{
		while(in.hasNextLine()){
			csvline=in.nextLine();
			String element=(new CSVParser()).parseLine(csvline)[0];
			map2.put(element,count);
			count++;
			
		}}
		catch(Exception e){
			System.out.println(csvline);
		}
		
		in.close();
		
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard"));
		in=new Scanner(new FileReader(prefix+"goldStandard_given"));
		while(in.hasNextLine()){
			String[] line=(new CSVParser()).parseLine(in.nextLine());
			out.println(map1.get(line[0])+" "+map2.get(line[1]));
		}
		
		out.close();
		in.close();
		
		
	}
	

	
}
