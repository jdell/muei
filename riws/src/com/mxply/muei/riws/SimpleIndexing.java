/* 
 * ***********************************************************
 * MUEI - RIWS - Practice 01: Simple indexing method using Lucene 
 * Author: W. Joel Castro Reynoso
 * Email: joemismito(at)gmail.com
 * ***********************************************************   
 */
package com.mxply.muei.riws;

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
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;


public class SimpleIndexing implements IModule {
	
	private int processedItems = 0;
	
	private void printSummary()
	{
		System.out.format("Operación finalizada. %d items indexados.", processedItems);
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
	
	@Override
	public void run(String[] params) {
		String ipath = params[0];
		String opath = params[1];

		processedItems = 0;
		
		try
		{
			File ifolder = new File(ipath);
			if (ifolder.exists())
			{		
				File[] files = ifolder.listFiles();
		
				IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));		
				IndexWriter writer = new IndexWriter(FSDirectory.open(new File(opath)), config);
		
				List<String> tags = Arrays.asList(".I", ".T", ".A", ".B", ".W");
				
				for (int i=0;i<files.length;i++)
				{
					File f = files[i];
					if (f.isFile())
					{
						BufferedReader br = new BufferedReader(new FileReader(f));
						String line = null;
						
						int currentIndex = -1;
						String values[] = new String[5];
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
									values[currentIndex] = line.replaceFirst(startWith,  "").trim();
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
					}
				}
				writer.commit();
				writer.close();
				
				printSummary();
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
	}
}
