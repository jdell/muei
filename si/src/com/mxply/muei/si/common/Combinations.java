package com.mxply.muei.si.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Combinations {

    private List<String> variableNames;
    private List<String> variableValues;

    public Combinations(Collection<String> variableNames, Collection<String> variableValues) {
        super();
        this.variableNames = new ArrayList<>(variableNames);
        this.variableValues = new ArrayList<>(variableValues);
    }

    public Collection<Map<String, String>> combinationsByVariable() {

        Collection<Map<String, String>> result = new HashSet<>();

        Collection<Collection<String>> combinations = combinations();

        for (Collection<String> combination : combinations) {
            Map<String, String> combinationByVariable = new HashMap<>();
            result.add(combinationByVariable);
            for (Iterator<String> combIt = combination.iterator(), varNameIt = variableNames.iterator(); combIt
                    .hasNext() && varNameIt.hasNext();) {
                combinationByVariable.put(varNameIt.next(), combIt.next());
            }
        }

        return result;
    }

    public Collection<Collection<String>> combinations() {

        Node root = buildNode(new Node(), variableNames.size());

        Collection<Collection<String>> combinations = new HashSet<Collection<String>>();
        traverse(root, new ArrayList<String>(), combinations);

        return combinations;
    }

    private Node buildNode(Node node, int size) {

        if (size > 0) {
            for (String value : variableValues) {
                node.childs.add(buildNode(new Node(value, node), size - 1));
            }
        }

        return node;
    }

    private void traverse(Node node, Collection<String> combination, Collection<Collection<String>> combinations) {

        if (node.childs.isEmpty()) {
            combinations.add(combination);
        }

        for (Node child : node.childs) {
            Collection<String> childCombination = new ArrayList<>();
            childCombination.addAll(combination);
            childCombination.add(child.value);
            traverse(child, childCombination, combinations);
        }

    }
}
