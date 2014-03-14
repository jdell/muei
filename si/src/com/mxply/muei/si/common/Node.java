package com.mxply.muei.si.common;

import java.util.ArrayList;
import java.util.Collection;

public class Node {

    public String value;
    public Node parent;
    public Collection<Node> childs = new ArrayList<>();

    public Node() {
        this(null, null);
    }

    public Node(String value, Node parent) {
        this.value = value;
        this.parent = parent;
    }

    @Override
    public String toString() {
        String result = "{ " + value;
        
        for(Node child: childs) {
            result += child.toString();
        }
        
        return result + " }";
    }
    
    

}
