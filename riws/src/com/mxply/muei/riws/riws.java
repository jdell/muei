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

		if (args.length!=2)
		{
			System.out.println("Usage: java riws inputfolder outputfolder");
			return;
		}
		IModule module = new SimpleIndexing();
		module.run(args);
	}

}
