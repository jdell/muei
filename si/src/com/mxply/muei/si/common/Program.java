package com.mxply.muei.si.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

public class Program {

    private final Set<Variable> variables = new TreeSet<>();
    private final List<Sentence> sentences = new ArrayList<>();
    private final Map<Variable, List<Sentence>> sentencesByVariable = new HashMap<>();
    private final Set<String> variableNames = new TreeSet<>();
    private final Set<String> variableValues = new TreeSet<>();

    private Program() {
    }

    public static Program loadFrom(File file)
    {
    	Program res = new Program();
    	res.load(file);
    	return res;
    }
    
    private void load(File file) {
    	//TODO:
        System.out.println("Programa");

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();

                Matcher matcher = Variable.VARIABLE_PATTERN.matcher(line);

                List<Variable> vars = new ArrayList<>();
                while (matcher.find()) {
                    vars.add(new Variable(matcher.group()));
                }

                if (vars.size() == 1) {
                    add(new Sentence(vars.get(0), new Condition()));
                } else {
                    if (line.contains("|")) {
                        for (int index = 1; index < vars.size(); index++) {
                            add(new Sentence(vars.get(0), new Condition(Arrays.asList(vars.get(index)))));
                        }
                    } else {
                        add(new Sentence(vars.get(0), new Condition(vars.subList(1, vars.size()))));
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!variableNames.isEmpty()) {

            Collection<Map<String, String>> combinations = new Combinations(variableNames, variableValues)
                    .combinationsByVariable();

            Collection<Sentence> newSentences = new TreeSet<>();
            Collection<Sentence> toDeleteSentences = new TreeSet<>();
            for (final Sentence sentence : sentences) {
                List<Variable> sentenceVariables = new ArrayList<>(sentence.rs.vars);
                sentenceVariables.add(sentence.ls);
                for (Variable variable : sentenceVariables) {
                    if (!variable.variables.isEmpty()) {
                        for (Map<String, String> combination : combinations) {
                            Sentence newSentence = nonVariableSentence(sentence, combination);
                            newSentences.add(newSentence);
                        }
                        toDeleteSentences.add(sentence);
                        break;
                    }
                }
            }

            add(newSentences);
            this.sentences.removeAll(toDeleteSentences);

        }

        System.out.println("Programa cargado");
    }

    private Sentence nonVariableSentence(Sentence sentence, Map<String, String> combination) {
        return new Sentence(nonVariable(sentence.ls, combination), nonVariable(sentence.rs, combination));
    }

    private Variable nonVariable(Variable variable, Map<String, String> combination) {

        Collection<String> variableValues = new ArrayList<String>(variable.values);
        for (String variableName : variable.variables) {
            variableValues.add(combination.get(variableName));
        }

        return new Variable(variable.name, variableValues);
    }

    private Collection<Variable> nonVariable(Collection<Variable> variables, Map<String, String> combination) {
        Collection<Variable> result = new ArrayList<>();
        for (Variable variable : variables) {
            result.add(nonVariable(variable, combination));
        }
        return result;
    }

    private Condition nonVariable(Condition condition, Map<String, String> combination) {
        return new Condition(nonVariable(condition.vars, combination));
    }

    private void add(Collection<Sentence> sentences) {
        for (Sentence sentence : sentences) {
            add(sentence);
        }
    }

    private void add(Sentence sentence) {

        System.out.println(sentence);

        sentences.add(sentence);

        List<Sentence> variableSentences = sentencesByVariable.get(sentence.ls);
        if (variableSentences == null) {
            variableSentences = new ArrayList<>();
            sentencesByVariable.put(sentence.ls, variableSentences);
        }
        variableSentences.add(sentence);

        add(sentence.ls);
        for (Variable variable : sentence.rs.vars) {
            add(variable);
        }

    }

    private void add(Variable variable) {
        variables.add(variable);
        variableValues.addAll(variable.values);
        variableNames.addAll(variable.variables);
    }

    public boolean isEmpty() {
        return sentences.isEmpty();
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public List<Sentence> getSentences(Variable var) {
        List<Sentence> sentences = sentencesByVariable.get(var);
        return sentences != null ? sentences : Collections.<Sentence> emptyList();
    }

}
