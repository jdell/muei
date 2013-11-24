package com.mxply.muei.riws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;


public class SimpleIndexing implements IModule {
	
	
	public class WorkingSet
	{
		final String ITAGS[] = {".I", ".T", ".A", ".B", ".W"};

		int currentToken = -1;
		int prevToken = -1;
		
		public Boolean isToken(String line)
		{	
			Boolean res = false;
			
			for(int i=0; i<ITAGS.length; i++)
			{
				res = _isToken(line, i);
				if (res) break;
			}
			return res;
		}
		private Boolean _isToken(String line, int i)
		{
			Boolean res = line.startsWith(ITAGS[i]);
			if (res)
			{
				prevToken = currentToken;
				currentToken = i;
			}
			return res;
		}
		private Boolean isFirstToken(String value)
		{
			return _isToken(value, 0);
		}
		/*
		public String extractValue(String line)
		{
			
		}*/
		
		StringBuilder sb = new StringBuilder();
		
		public void resetValue()
		{
			sb = new StringBuilder();
		}
		public void storeValue(String line)
		{
			sb.append(line);
		}
	}
	
	public class Cranfield
	{
		private String fieldI;
		private String fieldT;
		private String fieldA;
		private String fieldB;
		private String fieldW;
		
		public String getFieldT() {
			return fieldT;
		}

		public void setFieldT(String fieldT) {
			this.fieldT = fieldT;
		}

		public String getFieldA() {
			return fieldA;
		}

		public void setFieldA(String fieldA) {
			this.fieldA = fieldA;
		}

		public String getFieldB() {
			return fieldB;
		}

		public void setFieldB(String fieldB) {
			this.fieldB = fieldB;
		}

		public String getFieldW() {
			return fieldW;
		}

		public void setFieldW(String fieldW) {
			this.fieldW = fieldW;
		}


		public String getFieldI() {
			return fieldI;
		}

		public void setFieldI(String fieldI) {
			this.fieldI = fieldI;
		} 
	}
	
	private Boolean addDoc(IndexWriter w, Cranfield c) throws IOException {
		Boolean res = false;
		if (c!=null && c.getFieldI()!=null)
		{
		  Document doc = new Document();
		  doc.add(new TextField("ID", c.getFieldI(), Store.YES));
		  doc.add(new TextField("TITLE", c.getFieldT(), Store.YES));
		  doc.add(new TextField("AUTHOR", c.getFieldA(), Store.YES));
		  doc.add(new TextField("INSTITUTION", c.getFieldB(), Store.YES));
		  doc.add(new TextField("CONTENT", c.getFieldW(), Store.YES));
		  w.addDocument(doc);
		  res = true;
		}
		return res;
	}
	private Boolean addDoc(IndexWriter w, String[] values) throws IOException {
		Boolean res = false;
		if (values!=null)
		{
		  Document doc = new Document();
		  doc.add(new IntField("ID", Integer.parseInt(values[0]), Store.YES));
		  doc.add(new TextField("TITLE", values[1], Store.YES));
		  doc.add(new TextField("AUTHOR", values[2], Store.YES));
		  doc.add(new TextField("INSTITUTION", values[3], Store.YES));
		  doc.add(new TextField("CONTENT", values[4], Store.YES));
		  w.addDocument(doc);
		  res = true;
		}
		return res;
	}
	
	@Override
	public void run(String[] params) {
		String ipath = params[0];
		String opath = params[1];

	
		
try
{
	File ifolder = new File(ipath);
	if (ifolder.exists())
	{
		WorkingSet ws = new WorkingSet();
		
		File[] files = ifolder.listFiles();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));		
		IndexWriter writer = new IndexWriter(FSDirectory.open(new File(opath)), config);

		for (int i=0;i<files.length;i++)
		{
			File f = files[i];
			if (f.isFile())
			{
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = null;
				
				Cranfield cran = null;
				int currentIndex = -1;
				String values[] = new String[5];
				while ((line = br.readLine()) != null) 
				{
					if (line.length()>1)
					{						
						String startWith = line.substring(0, 2);
						switch (startWith) {
						case ".I":
							if (currentIndex>-1)
							{
								addDoc(writer, values);
								for	(int v=0; v<values.length;v++)
									values[v]="";
							}
							currentIndex = 0;							
							values[currentIndex] = line.replaceFirst(".I",  "").trim();
							System.out.println(line + " valor " + line);
							
							break;
						case ".T":		
							currentIndex = 1;		
							values[currentIndex] = line.replaceFirst(".T",  "").trim();			
							break;
						case ".A":	
							currentIndex = 2;	
							values[currentIndex] = line.replaceFirst(".A",  "").trim();					
							break;
						case ".B":		
							currentIndex = 3;	
							values[currentIndex] = line.replaceFirst(".B",  "").trim();				
							break;
						case ".W":	
							currentIndex = 4;	
							values[currentIndex] = line.replaceFirst(".W",  "").trim();					
							break;

						default:
							values[currentIndex] += line;
							break;
						}
					}
				}
				if (currentIndex>-1)
					addDoc(writer, values);
				
				br.close();
			}
		}
		writer.commit();
		writer.close();
		
	}
	else
	{
		System.out.format("Directorio %s no encontrado. Directorio actual: %s", ipath, new File(".").getCanonicalPath());
	}
}catch(CorruptIndexException ex)
{
	System.out.println("Describir excepcion");
	ex.printStackTrace();
}catch(LockObtainFailedException ex)
{
	System.out.println("Describir excepcion");
	ex.printStackTrace();
}catch(IOException ex)
{
	System.out.println("Describir excepcion");
	ex.printStackTrace();
}
		
		/*
		
		String modelRef[] = new String[5];
		//TODO: init model Ref
		
		String indexFolder = params[0];
		
		//-----------
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		
		DirectoryReader ireader = null;
		IndexSearcher isearcher = null;
		QueryParser iparser = null;
		
		/*
		try{					
		}catch(CorruptIndexException ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}catch(LockObtainFailedException ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}catch(IOException ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}
		for(int i=0; i<modelRef.length; i++)
		{
			Document doc = new Document();

			doc.add(new StringField("ID", modelRef[i], null));
			doc.add(new StringField("ID", modelRef[i], null));
			doc.add(new StringField("ID", modelRef[i], null));
			doc.add(new StringField("ID", modelRef[i], null));
			doc.add(new StringField("ID", modelRef[i], null));
			
		}
		*/
	}

}
