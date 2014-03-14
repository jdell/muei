package com.mxply.muei.si.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.mxply.muei.si.common.*;

public class BottomUpAlgorithm extends Algorithm {
	public BottomUpAlgorithm()
	{
		super("Bottom-Up (forward chaining)");
	}

	@Override
	protected void action() {

        Set<Variable> result = new TreeSet<>();

        //TODO: Print
        System.out.println("Asignaciones");

        for (Iterator<Sentence> programIterator = program.getSentences().iterator(); programIterator.hasNext();) {
            Sentence sentence = programIterator.next();
            if (sentence.eval(Collections.<Variable> emptyList())) {
                result.add(sentence.ls);
                programIterator.remove();
            }
        }

        Collection<Variable> previous = null;
        do {

            previous = new ArrayList<>(result);

            //TODO: Print
            System.out.println("Variables previas");

            for (Iterator<Sentence> sentenceIt = program.getSentences().iterator(); sentenceIt.hasNext();) {

                Sentence sentence = sentenceIt.next();

                if (result.contains(sentence.ls)) {
                    sentenceIt.remove();
                } else if (!sentence.rs.isEmpty()) {
                    for (Iterator<Variable> varIt = sentence.rs.vars.iterator(); varIt.hasNext();) {
                        Variable variable = varIt.next();
                        if (result.contains(variable)) {
                            varIt.remove();
                        }
                    }
                }
            }

            for (Sentence sentence : program.getSentences()) {

                if (sentence.eval(result)) {
                    result.add(sentence.ls);
                }
            }

        } while (!program.isEmpty() && !previous.containsAll(result));

		
	}

}
