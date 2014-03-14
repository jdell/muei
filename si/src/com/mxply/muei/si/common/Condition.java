package com.mxply.muei.si.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Condition {

    public final Collection<Variable> vars = new ArrayList<Variable>();

    public Condition() {
        this(Collections.<Variable> emptyList());
    }

    public Condition(Collection<Variable> vars) {
        this.vars.addAll(vars);
    }

    public boolean eval(Collection<Variable> actual) {

        boolean result = true;

        for (Variable var : vars) {
            result &= actual.contains(var);
        }

        return result;

    }

    public boolean isEmpty() {
        return vars.isEmpty();
    }

    public String toString() {
        String result = "";
        for (Variable var : vars) {
            result += var + ",";
        }
        return result.length() == 0 ? result : result.substring(0, result.length() - 1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((vars == null) ? 0 : vars.hashCode());
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
        Condition other = (Condition) obj;
        if (vars == null) {
            if (other.vars != null)
                return false;
        } else if (!vars.equals(other.vars))
            return false;
        return true;
    }

}
