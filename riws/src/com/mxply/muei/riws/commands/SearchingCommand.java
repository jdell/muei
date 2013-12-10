/* 
 * ***********************************************************
 * MUEI - RIWS - Practice 03.1: Index searching method using Lucene 
 * Author: W. Joel Castro Reynoso
 * Email: joemismito(at)gmail.com
 * ***********************************************************   
 */
package com.mxply.muei.riws.commands;

import java.io.File;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.mxply.muei.riws.common.Mutable;
import com.mxply.muei.riws.common.SearchBuilder;
import com.mxply.muei.riws.common.parser;

public class SearchingCommand extends Command{

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length>=2;
	}

	@Override
	public String getKey() {
		return "search";
	}

	@Override
	protected String getParams() {
		return "indexfolder top query";
	}

	@Override
	protected void action(String[] params) {
		try		{
			String ipath = params[0];
			File ifile = new File(ipath);
			if (!ifile.exists())
			{
				System.out.format("Folder not found [%s]. Current path: %s", ipath, new File(".").getCanonicalPath());
				return;
			}

			Directory idir = FSDirectory.open(ifile);			
			if (!DirectoryReader.indexExists(idir))
			{
				System.out.format("There is no Index in the specified folder [%s]. Current path: %s", ipath, new File(".").getCanonicalPath());
				return;
			}
						
			com.mxply.muei.riws.common.Mutable<Integer> top = new com.mxply.muei.riws.common.Mutable<Integer>() ;
			if (!parser.tryParseInt(params[1], top))
			{
				System.out.println(toString());
				return;
			}
			
			SearchBuilder sb = new SearchBuilder();
			TopDocs topDocs = sb.search(idir, com.mxply.muei.riws.common.parser.join(params, " ", 2), top.get(), new DefaultSimilarity());
			DirectoryReader reader = DirectoryReader.open(idir);
			
			int processedItems = Math.min(top.get(), topDocs.totalHits);
			for (int i = 0; i < processedItems ; i++) {
				Document doc = reader.document(topDocs.scoreDocs[i].doc);
				System.out.format("%s\t%s\t%s\t%s\n", i+1, doc.get("ID"),topDocs.scoreDocs[i].score, doc.get("TITLE"));
			}
			reader.close();
			idir.close();
			
			System.out.format("Operation completed. %d items.\n", processedItems);			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

}
