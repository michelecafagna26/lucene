package pack;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Cran {
	
	private static String[] fields={".I",".T",".A",".B",".W"};
	
	public static void main(String[] args) throws Exception{
		
		File inputFolder= new File("cran");
		File cranIndex= new File("cranIndex");
		File query= new File("cran\\cran.qry");
		
		//index(inputFolder,cranIndex);
		
		//System.out.print(getQueryResult(query, cranIndex));
		
		long startTime = System.currentTimeMillis();
		
		String data=getQueryResult(query, cranIndex);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Execution time: "+(endTime-startTime)+" ms");
		
		PrintWriter writer= new PrintWriter("out\\cran.out", "UTF-8");
		
		writer.print(data);
		writer.close();
		
	}
	
	/**
	 * index cran collection 
	 * @param inputDir
	 * @param indexFile
	 * @throws IOException
	 */
	public static void index(File inputDir, File indexFile) throws IOException{
		
		Version version= Version.LATEST;
		Analyzer analyzer= new StandardAnalyzer(version, EnglishAnalyzer.getDefaultStopSet());
		
		Directory directory= FSDirectory.open(indexFile);
		
		IndexWriterConfig config= new IndexWriterConfig(version, analyzer);
		
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		
		IndexWriter writer= new IndexWriter(directory, config);
		
		if(inputDir.isDirectory()){
			
			File[] fileLists= inputDir.listFiles();
			File cranFile=null;
			
			//get cran collection
			for(File file: fileLists){
				
				if(file.getPath().endsWith(".1400")){
					
					cranFile=file;
					break;
				}
			}
			
			try{
			
				ArrayList<String[]> content=getContent(load(cranFile));
			
				Iterator<String[]> it=content.iterator();
				
				while(it.hasNext()){
					
					Document doc= new Document();
					
					String[] row= it.next();
					
					for(int i=0;i<row.length;i++){
						
						doc.add(new TextField(fields[i],row[i],Field.Store.YES));
					}
					
					writer.addDocument(doc);
				}
				
				
			}catch(Exception e){
				
				Logger.getLogger(Cran.class.getName()).log(Level.SEVERE, null, e);
			}
			
			
		}
		
		writer.close();
	}
	
	/**
	 * leggi un file di testo
	 * @param file
	 * @return stringa che rappresenta il file
	 * @throws Exception
	 */
	 static String load(File file) throws Exception{

		BufferedReader in= new BufferedReader(new FileReader(file));
		StringBuffer buff= new StringBuffer();

		while(in.ready()){

			String line= in.readLine();
			buff.append(line);
		}

		in.close();

		return buff.toString();

	}
	
	 /**
	  * return a list of element from the text. Elements are string's array;
	  * @param text
	  * @return
	  */
	 static ArrayList<String[]> getContent(String text){
		
		ArrayList<String[]> resultSet= new ArrayList<String[]>();
		
		  String[] splitted= text.split(fields[0]);
		
		for(int i=1;i<splitted.length;i++){
			
			String row=".I".concat(splitted[i]);
			
			resultSet.add(getElement(row));
		}
		
		return resultSet;
	}
	 
	 /**
	  * get single field from a row extracted from text
	  * @param row
	  * @return element as array of strings, where each string is a field
	  */
	 static String[] getElement(String row){
		 
		 String[] element= new String[5];
		 
		 for(int i=element.length-1;i>=0;i--){
			 
			 element[i]=row.substring(row.lastIndexOf(fields[i]));
			 row=row.replace(element[i], "");
		 }
		 
		 for(int i=0;i<element.length;i++){
			 
			element[i]= element[i].replaceAll(fields[i],"").trim();
		 }
		 
		 return element;
		 
	 }
	 
	 /**
	  * perform a search in the index
	  * @param query
	  * @param query
	  * @param numOfResults
	  * @return
	 * @throws IOException,ParseException
	  */
	 static ScoreDoc[] search( String q, String field, int numOfResults, File index) throws ParseException, IOException{
		 
		 Query query= new QueryParser(field, new StandardAnalyzer(Version.LATEST, EnglishAnalyzer.getDefaultStopSet())).parse(q);
		 
		 Directory indexFolder= FSDirectory.open(index);
		 
		 IndexReader reader= DirectoryReader.open(indexFolder);
		 
		 IndexSearcher searcher= new IndexSearcher(reader);
		 
		 return searcher.search(query,numOfResults).scoreDocs;
		 
	 }
	 
	 /**
	  * return a list of queries from a semi-structured String text
	  * @param text
	  * @return
	  */
	 static ArrayList<String[]> getQueryList(String text){
		 
		 ArrayList<String[]> result= new ArrayList<String[]>();
		 
		 //avoid queryparser exception
		 text=text.replace("?", "");
		 
		 String[] splitted= text.split(fields[0]);
		 
		 for(int i=1;i<splitted.length;i++){
			 
			 String[] element= splitted[i].split(fields[4]);
			 
			 element[0]= element[0].trim();
			 element[1]=element[1].trim();
			 
			 result.add(element);

		 }
		 
		 return result;
		 
	 }
	 
	 /**
	  * return the output of search
	  * @param query
	  * @param index
	  * @return
	  * @throws Exception
	  */
	 static String getQueryResult(File query, File index) throws Exception{
		 
		 String field= fields[4];
		 
		 ArrayList<String[]> queryList= getQueryList(load(query));
		 
		 Iterator<String[]> it= queryList.iterator();
		 
		 StringBuilder result= new StringBuilder();
		 
		 
		 
		 while(it.hasNext()){
			 
			 int counter=0;
			 
			 String[] currentQuery= it.next();
			 
			 ScoreDoc[] resultDoc= search(currentQuery[1], field, 800 ,index);
			 
			 for(int i=0;i<resultDoc.length;i++){
				 
				 result.append(currentQuery[0]+" 0 "+(resultDoc[i].doc+1)+" "+counter+" "+ resultDoc[i].score+" 1 \n");
				 
				 counter++;
			 }
		 }
		 
		 return result.toString();
		 
	 }
	 
	

}
