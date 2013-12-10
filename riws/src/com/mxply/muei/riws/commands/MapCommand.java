package com.mxply.muei.riws.commands;

import java.io.File;

public class MapCommand extends Command {

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length==2;
	}

	@Override
	public String getKey() {
		return "map";
	}

	@Override
	protected String getParams() {
		return "inputfile qrelfile";
	}

	@Override
	protected void action(String[] params) {
		// TODO Auto-generated method stub
		//qrel: query-number 0 document-id relevance
try {


	String qrelpath = params[1];
	File qrelfile = new File(qrelpath);
	if (!qrelfile.exists() || !qrelfile.isFile())
	{			
		System.out.format("File not found [%s ]. Current folder: %s\n", qrelpath, new File(".").getCanonicalPath());
		return;
	}
	
} catch (Exception ex) {
	ex.printStackTrace();
}
		
	}

}
