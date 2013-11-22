package com.mxply.muei.riws;

import java.io.File;
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

	@Override
	public void run(String[] params) {
		String ifolder = params[0];
		String ofolder = params[1];

		
		
		
		/*
		
		String modelRef[] = new String[5];
		//TODO: init model Ref
		
		String indexFolder = params[0];
		
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));
		
		IndexWriter writer = null;
		*/
		//-----------
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		
		DirectoryReader ireader = null;
		IndexSearcher isearcher = null;
		QueryParser iparser = null;
		String iFields[] = {"I", "T", "A", "B", "W"};
		
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
			String text = "testing";
			
			//IndexReader ts= IndexReader.open(directory);
			
			System.out.println("Test: " + text);
			
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
