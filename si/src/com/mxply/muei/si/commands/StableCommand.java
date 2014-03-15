package com.mxply.muei.si.commands;

import com.mxply.muei.si.algorithms.IterateStableAlgorithm;

public class StableCommand extends AlgorithmCommand  {

	public StableCommand(){
		algorithm = new IterateStableAlgorithm();
	}
	@Override
	public String getKey() {
		return "stable";
	}
}
