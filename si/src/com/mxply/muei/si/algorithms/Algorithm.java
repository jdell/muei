package com.mxply.muei.si.algorithms;

import java.util.Set;

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
        print(result);
	}
	protected void print(Set<Variable> r)
	{
        System.out.printf("Resultado: %s\n", r);
	}
}
