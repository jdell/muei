package com.mxply.muei.si.algorithms;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.mxply.muei.si.Environment;
import com.mxply.muei.si.common.*;

public abstract class Algorithm {
	Set<Variable> result;
	Program program;
	
	private String name;
	public String getName()
	{
		return this.name;
	}
	public Algorithm(String name)
	{
		this.name = name;
	}
	public void execute(Program program)
	{
		this.program = program;
		System.out.printf("**********************************\n");
		System.out.printf("Algoritmo: %s\n", getName());
		System.out.printf("**********************************\n");
		action();
		print();
		System.out.printf("**********************************\n");
	}
	protected abstract void action();
	protected void print()
	{
		System.out.print("Resultado final: [");
		for (Variable v : result) {
	    	if (v.toString().contains(Environment.preguntaTopDown))
	    		System.out.print(v.toString());
		}
		System.out.println("]");
        //print(result, "Resultado final");
	}
	protected void print(Collection<Variable> r, String txt)
	{
		if (r==null) return;
        System.out.printf("%s: %s\n",txt, r);
	}
	protected void print(Set<Variable> r, String txt)
	{
		if (r==null) return;
        System.out.printf("%s: %s\n",txt, r);
	}
}
