/* 
 * ***********************************************************
 * MUEI - RIWS - Practice 01: Simple indexing method using Lucene 
 * Author: W. Joel Castro Reynoso
 * Email: joemismito(at)gmail.com
 * ***********************************************************   
 */
package com.mxply.muei.riws.commands;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.LockObtainFailedException;

import com.mxply.muei.riws.common.IndexBuilder;

public class IndexingCommand extends Command {
	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length==2;
	}

	@Override
	public String getKey() {
		return "index";
	}


	@Override
	protected void action(String params[]) {
		String ipath = params[0];
		String opath = params[1];
		
		try
		{
			File ifile = new File(ipath);
			if (ifile.exists() && ifile.isFile())
			{			
				IndexBuilder ib = new IndexBuilder();
				ib.build(ifile.getPath(), new DefaultSimilarity(), opath);
				
				System.out.format("Operation completed. %d items.\n", ib.getProcessedItems());
			}
			else
			{
				System.out.format("File not found [%s ]. Current folder: %s\n", ipath, new File(".").getCanonicalPath());
			}
		}catch(CorruptIndexException ex)
		{
			ex.printStackTrace();
		}catch(LockObtainFailedException ex)
		{
			ex.printStackTrace();
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}		
	}

	@Override
	protected String getParams() {
		return "inputfile outputfolder";
	}
	
}
