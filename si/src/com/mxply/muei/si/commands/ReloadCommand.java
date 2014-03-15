package com.mxply.muei.si.commands;

import com.mxply.muei.si.Environment;

public class ReloadCommand extends LoadCommand  {

	@Override
	public Boolean canExecute(String[] params) {
		return Environment.Current().getProgram()!=null;
	}

	@Override
	public String getKey() {
		return "reload";
	}

	@Override
	protected String getParams() {
		return "";
	}

	@Override
	protected void action(String[] params) {
		super.action(new String[]{Environment.Current().getProgram().getPath()});
	}

}
