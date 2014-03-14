package com.mxply.muei.si.common;


import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable implements Comparable<Variable> {

    private static final Pattern VARIABLE_PARAMS_PATTERN_ = Pattern.compile("[a-zA-Z0-9]+");
    public static final Pattern VARIABLE_PATTERN = Pattern.compile("[a-zA-Z0-9]+(?:\\([a-zA-Z0-9,]+\\))*");

    public final String name;
    public final Collection<String> variables = new ArrayList<String>();
    public final Collection<String> values = new ArrayList<String>();

    public Variable(String name) {
        
        int startIndex = name.indexOf("(");
        int endIndex = name.indexOf(")");
        
        if (startIndex != -1 && endIndex != -1) {
            
            this.name = name.substring(0, startIndex);
            
            Matcher matcher = VARIABLE_PARAMS_PATTERN_.matcher(name.substring(startIndex, endIndex));
            while (matcher.find()) {
                
                String variable = matcher.group();
                if (Character.isUpperCase(variable.charAt(0))) {
                    this.variables.add(variable.trim());
                } else {
                    this.values.add(variable.trim());
                }
            }

        } else {
            this.name = name;
        }
    }
    
    public Variable(String name, Collection<String> values) {
        this.name = name;
        this.values.addAll(values);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Variable other = (Variable) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }

    @Override
    public int compareTo(Variable o) {
        return toString().compareToIgnoreCase(o.toString());
    }

    @Override
    public String toString() {
        String result = name;
        if (!values.isEmpty() || !variables.isEmpty()) {
            result += "(";
        }
        for (String value : values) {
            result += value + ",";
        }
        for (String value : variables) {
            result += value + ",";
        }
        if (!values.isEmpty() || !variables.isEmpty()) {
            result = result.substring(0, result.length() - 1) + ")";
        }
        return result;
    }

}
