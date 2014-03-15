package com.mxply.muei.si.commands;

import com.mxply.muei.si.Environment;
import com.mxply.muei.si.algorithms.Algorithm;

public abstract class AlgorithmCommand extends Command {

	protected Algorithm algorithm = null;
	
	@Override
	public Boolean canExecute(String[] params) {
		return algorithm!=null && Environment.Current().getProgram()!=null;
	}

	@Override
	protected String getParams() {
		return "";
	}

	@Override
	protected void action(String[] params) {
		algorithm.execute(Environment.Current().getProgram());
	}

}
