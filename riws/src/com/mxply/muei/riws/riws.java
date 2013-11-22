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
