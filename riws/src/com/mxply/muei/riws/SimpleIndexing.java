package com.mxply.muei.riws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
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
	
	
	//private Boolean isTocken
	
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
		
		for (int i=0;i<files.length;i++)
		{
			File f = files[i];
			if (f.isFile())
			{
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = null;
				while ((line = br.readLine()) != null) 
				{
					if (ws.isToken(line))
					{
						ws.resetValue();						
						if (ws.isFirstToken(line))
						{
							
						}
						//System.out.println(line);
					}
					
					ws.storeValue(line);
					
				}
				br.close();
			}
		}
	}
	else
	{
		System.out.format("Directorio %s no encontrado. Directorio actual: %s", ipath, new File(".").getCanonicalPath());
	}
}catch(Exception ex)
{
	System.out.println("Describir excepcion");
	ex.printStackTrace();
}
		
		/*
		
		String modelRef[] = new String[5];
		//TODO: init model Ref
		
		String indexFolder = params[0];
		
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));
		
		IndexWriter writer = null;
		//-----------
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		
		DirectoryReader ireader = null;
		IndexSearcher isearcher = null;
		QueryParser iparser = null;
		
		/*
		try{			
			//Steps:
			//1.- read input
			//ireader = DirectoryReader.open(FSDirectory.open(new File(ifolder)));
			//isearcher = new IndexSearcher(ireader);
			//iparser = new QueryParser(Version.LUCENE_40, iFields[0], analyzer);
			
			//2.- parse it
			
			//3.- write output
			
			//writer = new IndexWriter(FSDirectory.open(new File(indexFolder)), config);
			
			//IndexReader ts= IndexReader.open(directory);
		
			//ireader.close();			
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
