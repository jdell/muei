package com.mxply.muei.riws.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class IndexBuilder {
	
	int processedItems = 0;
	
	public int getProcessedItems() {
		return processedItems;
	}
	public void setProcessedItems(int processedItems) {
		this.processedItems = processedItems;
	}
	private Boolean addDocument(IndexWriter w, String[] values) throws IOException {
		Boolean res = false;
		if (values!=null)
		{
			  Document doc = new Document();
			  doc.add(new StringField("ID", values[0], Store.YES));
			  doc.add(new TextField("TITLE", values[1].trim(), Store.YES));
			  doc.add(new TextField("AUTHOR", values[2].trim(), Store.YES));
			  doc.add(new StringField("INSTITUTION", values[3].trim(), Store.YES));
			  doc.add(new TextField("CONTENT", values[4].trim(), Store.YES));
			  w.addDocument(doc);
			  res = true;
			  
			  processedItems++;
		}
		return res;
	}
	public Directory build(String crandocfile, Similarity similarity)
	{		
		RAMDirectory dir = null;
		try {
			dir = new RAMDirectory();
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));
			config.setSimilarity(similarity);
			IndexWriter writer = new IndexWriter(dir, config);
			List<String> tags = Arrays.asList(".I", ".T", ".A", ".B", ".W");

			BufferedReader br = new BufferedReader(new FileReader(crandocfile));
			String line = null;
			
			int currentIndex = -1;
			String values[] = new String[5];
			for	(int v=0; v<values.length;v++)
				values[v]="";
			
			int tagIndex = -1;
			while ((line = br.readLine()) != null) 
			{
				if (line.length()>1)
				{						
					String startWith = line.substring(0, 2);
					if ((tagIndex=tags.indexOf(startWith))>-1)
					{
						if (tagIndex==0 && currentIndex>-1)
						{
							addDocument(writer, values);
							for	(int v=0; v<values.length;v++)
								values[v]="";
						}
						currentIndex = tagIndex;							
						values[currentIndex] += line.replaceFirst(startWith,  "").trim();
					}
					else{
						if (currentIndex>-1)
							values[currentIndex] += " " + line;
					}
				}
			}
			if (currentIndex>-1)
				addDocument(writer, values);
			
			br.close();
			
			writer.commit();
			writer.close();
					
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		return dir;
	}
	public void build(String crandocfile, Similarity similarity, String outputpath)
	{
		IndexWriter writer = null;
		try { 
			Directory dir = build(crandocfile, similarity);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));
			writer = new IndexWriter(FSDirectory.open(new File(outputpath)), config);
			
			writer.addIndexes( dir ); 

			writer.commit();
			writer.close(); 
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}