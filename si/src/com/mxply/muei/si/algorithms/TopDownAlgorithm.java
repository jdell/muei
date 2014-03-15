package com.mxply.muei.si.algorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.mxply.muei.si.common.*;

public class TopDownAlgorithm  extends Algorithm{
	public TopDownAlgorithm()
	{
		super("Top-Down (backward chaining)");
	}

    private Collection<Variable> eval(Variable var, Collection<Variable> result,
            Collection<Variable> evaluatedVariables, int deep) {

        deep++;

        //TODO: Print
        System.out.println("Variable " + var);

        evaluatedVariables.add(var);

        for (Sentence sentence : program.getSentences(var)) {

            if (sentence.eval(result)) {
                result.add(sentence.ls);
            } else {

                for (Variable conditionVar : sentence.rs.vars) {
                    if (!evaluatedVariables.contains(conditionVar)) {
                        result.addAll(eval(conditionVar, result, evaluatedVariables, deep));
                    }
                }

                if (sentence.eval(result)) {
                    result.add(sentence.ls);
                }

            }

        }

        return result;
    }

	@Override
	protected void action() {
        Set<Variable> result = new TreeSet<>();

        for (Variable variable : program.getVariables()) {
            result.addAll(eval(variable, result, new HashSet<Variable>(), 0));
        }
        this.result = result;
	}
}
