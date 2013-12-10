package com.mxply.muei.riws.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.mxply.muei.riws.common.IndexBuilder;
import com.mxply.muei.riws.common.SearchBuilder;

public class TrecCommand extends Command{

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length==2;
	}

	@Override
	public String getKey() {
		return "trec";
	}

	@Override
	protected String getParams() {
		return "indexFolder queryfile";
	}

	@Override
	protected void action(String[] params) {
		try{
			String indexpath = params[0];
			File indexfile = new File(indexpath);
			if (!indexfile.exists())
			{
				System.out.format("Folder %s not found. Current path: %s\n", indexpath, new File(".").getCanonicalPath());
				return;
			}
			
			Directory idir = FSDirectory.open(indexfile);			
			if (!DirectoryReader.indexExists(idir))
			{
				System.out.format("There is no Index in the specified folder [%s]. Current path: %s\n", indexpath, new File(".").getCanonicalPath());
				return;
			}

			String querypath = params[1];
			File queryfile = new File(querypath);
			if (!queryfile.exists() || !queryfile.isFile())
			{			
				System.out.format("File not found [%s ]. Current folder: %s\n", querypath, new File(".").getCanonicalPath());
				return;
			}

			List<String> tags = Arrays.asList(".I", ".W");
			BufferedReader br = new BufferedReader(new FileReader(queryfile));
			String line = null;
			String querystring = null;
			
			String values[] = new String[2];
			for	(int v=0; v<values.length;v++)
				values[v]="";
			
			SearchBuilder sb = new SearchBuilder();
			int currentIndex = -1;
			int tagIndex = -1;
			int querynumber = 1;
			TopDocs topDocs = null;
			DirectoryReader reader = DirectoryReader.open(idir);
			while ((line = br.readLine()) != null) 
			{
				if (line.length()>1)
				{						
					String startWith = line.substring(0, 2);
					if ((tagIndex=tags.indexOf(startWith))>-1)
					{
						if (tagIndex==0 && currentIndex>-1)
						{
							addQuery(values, querynumber, sb, idir, reader);
							querynumber++;
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
				addQuery(values, querynumber, sb, idir, reader);
			
			br.close();		
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}		
		finally{
			logcommit();
		}
	}
	private void addQuery(String values[], int querynumber, SearchBuilder sb, Directory idir, DirectoryReader reader)
	{
		try {
			String querystring = values[1];
			log(String.format("Query: %s", querystring));
			TopDocs topDocs = sb.search(idir, querystring, Integer.MAX_VALUE, new DefaultSimilarity());
			for (int i = 0; i < topDocs.totalHits ; i++) {
				Document doc = reader.document(topDocs.scoreDocs[i].doc);
				//query-number q0 document-id rank score exp
				log(String.format("%4d\tq0\t%4s\t%4d\t%10f\texp", querynumber, doc.get("ID"), i+1, topDocs.scoreDocs[i].score));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
