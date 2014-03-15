package com.mxply.muei.si.commands;

import com.mxply.muei.si.Environment;

public class PrintCommand  extends Command {

	@Override
	public Boolean canExecute(String[] params) {
		return Environment.Current().getProgram()!=null;
	}

	@Override
	public String getKey() {
		return "print";
	}

	@Override
	protected String getParams() {
		return "";
	}

	@Override
	protected void action(String[] params) {
		Environment.Current().getProgram().print();
	}

}
