package com.mxply.muei.si.common;

import java.util.Collection;

public class Sentence implements Comparable<Sentence> {

    public final Variable ls;
    public final Condition rs;

    public Sentence(Variable ls) {
        this(ls, new Condition());
    }

    public Sentence(Variable ls, Condition rs) {
        super();
        this.ls = ls;
        this.rs = rs;
    }

    @Override
    public String toString() {

        String result = "";
        if (rs.isEmpty())
            result = ls + ".";
        else
            result = ls + "<-" + rs + ".";
        return result;
    }

    public boolean eval(Collection<Variable> actual) {
        return rs.eval(actual);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ls == null) ? 0 : ls.hashCode());
        result = prime * result + ((rs == null) ? 0 : rs.hashCode());
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
        Sentence other = (Sentence) obj;
        if (ls == null) {
            if (other.ls != null)
                return false;
        } else if (!ls.equals(other.ls))
            return false;
        if (rs == null) {
            if (other.rs != null)
                return false;
        } else if (!rs.equals(other.rs))
            return false;
        return true;
    }

    @Override
    public int compareTo(Sentence o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

}
