package com.mxply.muei.riws.commands;

public class MapCommand extends Command {

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length==1;
	}

	@Override
	public String getKey() {
		return "map";
	}

	@Override
	protected String getParams() {
		// TODO Auto-generated method stub
		return "inputfolder ??? ???";
	}

	@Override
	protected void action(String[] params) {
		// TODO Auto-generated method stub
		//qrel: query-number 0 document-id relevance

		
	}

}
