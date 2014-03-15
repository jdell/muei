package com.mxply.muei.si.algorithms;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import com.mxply.muei.si.common.*;

public class IterateStableAlgorithm extends Algorithm{

	public IterateStableAlgorithm(){
		super("Stable");
	}
	
	@Override
	protected void action() {
        Set<Variable> previous = new TreeSet<>();
        Set<Variable> actual = new TreeSet<>();

        do {
        	System.out.println(Arrays.toString(actual.toArray()));
            previous.addAll(actual);

            for (Sentence sentence : program.getSentences()) {

                if (sentence.eval(actual)) {
                    actual.add(sentence.ls);
                }
            }

        } while (!previous.containsAll(actual));
        result = actual;
	}
}
