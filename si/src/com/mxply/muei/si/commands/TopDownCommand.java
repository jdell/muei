package com.mxply.muei.si.commands;

import com.mxply.muei.si.algorithms.TopDownAlgorithm;

public class TopDownCommand extends AlgorithmCommand  {

	public TopDownCommand()
	{
		algorithm = new TopDownAlgorithm();
	}
	@Override
	public String getKey() {
		return "topdown";
	}

}
