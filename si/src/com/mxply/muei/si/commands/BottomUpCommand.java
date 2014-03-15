package com.mxply.muei.si.commands;

import com.mxply.muei.si.algorithms.BottomUpAlgorithm;

public class BottomUpCommand extends AlgorithmCommand  {

	public BottomUpCommand(){
		algorithm = new BottomUpAlgorithm();
	}
	@Override
	public String getKey() {
		return "bottomup";
	}

}
