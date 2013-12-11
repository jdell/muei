package com.mxply.muei.riws.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import com.mxply.muei.riws.common.IndexBuilder;
import com.mxply.muei.riws.common.SearchBuilder;

public class MapCommand extends Command {

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length==3;
	}

	@Override
	public String getKey() {
		return "map";
	}

	@Override
	protected String getParams() {
		return "collectionfile queryfile qrelfile";
	}

	@Override
	protected void action(String[] params) {
		//qrel: query-number 0 document-id relevance
		try {
		
			String collectionpath = params[0];
			File collectionfile = new File(collectionpath);
			if (!collectionfile.exists() || !collectionfile.isFile())
			{			
				System.out.format("File not found [%s ]. Current folder: %s\n", collectionpath, new File(".").getCanonicalPath());
				return;
			}
		
			String querypath = params[1];
			File queryfile = new File(querypath);
			if (!queryfile.exists() || !queryfile.isFile())
			{			
				System.out.format("File not found [%s ]. Current folder: %s\n", querypath, new File(".").getCanonicalPath());
				return;
			}
			
			String qrelpath = params[2];
			File qrelfile = new File(qrelpath);
			if (!qrelfile.exists() || !qrelfile.isFile())
			{			
				System.out.format("File not found [%s ]. Current folder: %s\n", qrelpath, new File(".").getCanonicalPath());
				return;
			}
			
			
			//1.- read qrel
			Hashtable<String, List<String>> qrels = getQRels(qrelfile);
			
			//2.- read queries
			List<String> queries = getQueries(queryfile);
			
			//3.- loop
			List<Similarity> similarities = new ArrayList<Similarity>();
			similarities.add(new DefaultSimilarity());
			similarities.add(new LMDirichletSimilarity(10));
			similarities.add(new LMDirichletSimilarity(100));
			similarities.add(new LMDirichletSimilarity(200));
			similarities.add(new LMDirichletSimilarity(500));
			similarities.add(new LMDirichletSimilarity(1000));
			similarities.add(new LMDirichletSimilarity(2000));
			similarities.add(new LMDirichletSimilarity(5000));
			
			float map, apq, ap;
			Integer hit, missed;
			String querynumber;
			IndexBuilder ib = new IndexBuilder();
			SearchBuilder sb = new SearchBuilder();
			Directory dir = null;
			TopDocs topDocs = null;
			StringBuilder strb = null;
			for (Similarity similarity : similarities) {
				//3.1 index
				dir = ib.build(collectionfile.getPath(), similarity);
				DirectoryReader reader = DirectoryReader.open(dir);
				ap = 0;
				strb = new StringBuilder();
				for (int i = 0; i < queries.size(); i++) {
					querynumber = Integer.toString(i+1);
					if (!qrels.containsKey(querynumber))
						continue;
										
					//3.2 search docs
					topDocs = sb.search(dir, queries.get(i), Integer.MAX_VALUE, similarity);
					
					//3.3 compare qrel
					hit = 0;
					missed = 0;
					apq = 0;
					for (int h = 0; h < topDocs.totalHits ; h++) {
						Document doc = reader.document(topDocs.scoreDocs[h].doc);
						if (qrels.get(querynumber).contains(doc.get("ID")))
						{
							hit++;
							apq+= (float)hit/(hit+missed);
						}
						else
							missed++;
						
						if (hit==qrels.get(querynumber).size())
							break;
					}	
					ap += apq;
					strb.append(String.format("Q%s\t:\t%f\tDocs:\t{%s}\n", querynumber, apq, com.mxply.muei.riws.common.parser.join(qrels.get(querynumber), ", ", 0)));
				}
				//3.4 calc map
				map = ap / queries.size();
				//3.5 print map					
				log(String.format("Model:%20s\tMAP: %f", similarity.toString(), map));
				log(strb.toString(), false);
				strb = new StringBuilder();
				
				reader.close();
				dir.close();
			}
			logcommit();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	private Hashtable<String, List<String>> getQRels(File qrelfile)
	{
		Hashtable<String, List<String>> qrels = new Hashtable<String, List<String>>();
		try {

			BufferedReader br = new BufferedReader(new FileReader(qrelfile));
			String line = null, querynumber=null, docid=null, rel=null;
			String parts[] = null;
			while ((line = br.readLine()) != null) 
			{
				parts = line.split(" ");
				querynumber = parts[0];
				docid = parts[1];
				rel = parts[2];
				if (!qrels.containsKey(querynumber))
					qrels.put(querynumber, new ArrayList<String>());
				if (!qrels.get(querynumber).contains(docid))
					qrels.get(querynumber).add(docid);
			}
			br.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return qrels;
		
	}
	private List<String> getQueries(File queryfile)
	{
		//refactorize!
		List<String> queries = new ArrayList<String>();
		try {
			List<String> tags = Arrays.asList(".I", ".W");
			BufferedReader br = new BufferedReader(new FileReader(queryfile));
			String line = null;
			
			String values[] = new String[2];
			for	(int v=0; v<values.length;v++)
				values[v]="";
			
			int currentIndex = -1;
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
							queries.add(values[1]);
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
				queries.add(values[1]);
			
			br.close();	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return queries;
	}
}
