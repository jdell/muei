package com.mxply.muei.si.commands;

import com.mxply.muei.si.Environment;

public class ChangeDirectoryCommand extends Command  {

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length>0;
	}

	@Override
	public String getKey() {
		return "cd";
	}

	@Override
	protected String getParams() {
		return "directory";
	}

	@Override
	protected void action(String[] params) {
		Environment.Current().setDirectory(params[0]);
	}

}
