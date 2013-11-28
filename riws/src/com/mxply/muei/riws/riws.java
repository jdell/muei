/* 
 * ***********************************************************
 * MUEI - RIWS - Practice 01: Simple indexing method using Lucene 
 * Author: W. Joel Castro Reynoso
 * Email: joemismito(at)gmail.com
 * ***********************************************************   
 */
package com.mxply.muei.riws;


public class riws {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length<1)
		{
			showTheWay();
			return;			
		}
				
		String practiceID = args[0];
		String params[] = new String[args.length-1];		
		System.arraycopy(args, 1, params, 0, params.length);
		
		IModule module;
		switch(practiceID)
		{
			case "1":
				module = new SimpleIndexing();
			break;
			case "2":
				module = new SimpleSearching();
			break;
			default:
				showTheWay();
				return;
		}		
		module.run(params);
	}
	private static void showTheWay()
	{
		System.out.println("Usage: java riws practiceID [params...]");
		System.out.println("practiceID:");
		System.out.println("1: Simple Indexing");
		System.out.println("2: Simple Searching");
		System.out.println("");
	}

}
