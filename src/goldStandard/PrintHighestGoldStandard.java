package goldStandard;

import java.io.*;
import java.util.*;

public class PrintHighestGoldStandard {

	
	static String dataset="Restaurants";
	static String prefix="/host/heteroDatasets/eswc1/results/"+dataset+"/randomforest/";
	
	static String[] files={prefix+"run1.txt",prefix+"run2.txt",prefix+"run3.txt",prefix+"run4.txt",prefix+"run5.txt",prefix+"run6.txt",prefix+"run7.txt"};
	
	
	
	public static void main(String[] args)throws IOException{
		printBestFScoresFile();
	}
	
	public static void printBestFScoresFile()throws IOException{
		double[] r=new double[files.length];
		double[] p=new double[files.length];
		double[] f=new double[files.length];
		int count=-1;
		for(String sup:files){
			count++;
			Scanner in=new Scanner(new FileReader(sup));
			
			while(in.hasNextLine()){
				String[] recprec=in.nextLine().split(",");
				double rec=Double.parseDouble(recprec[0]);
				double prec=Double.parseDouble(recprec[1]);
				double fs=0;
				if(!(rec==0||prec==0))
					fs=2*rec*prec/(rec+prec);
				if(fs>f[count]){
					f[count]=fs;
					r[count]=rec;
					p[count]=prec;
				}
				
			}
			in.close();
		}
		System.out.println("Dataset,Run,Recall,Precision,FScore");
		for(int i=0; i<files.length; i++){
			System.out.println(dataset+","+(i+1)+","+r[i]+","+p[i]+","+f[i]);
			
		}
		
		
		
	}
}
