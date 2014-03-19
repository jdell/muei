package com.mxply.muei.si;

import com.mxply.muei.si.common.Program;

public class Environment{
	private static Environment _instance = null;
	protected Environment()
	{
		 directory = ".";
	}	
	public static String preguntaTopDown = "";//"a(1,3,";
	public static Environment Current()
	{
		if (_instance==null)
			_instance = new Environment();
		
		return _instance;
	}
	
	private String directory;
	public String getDirectory()
	{
		return directory;
	}
	public void setDirectory(String v)
	{
		this.directory = v;
	}
	public String getFullPath(String filename)
	{
		return  Environment.Current().getDirectory() + "/" + filename;
	}
	private Program program;
	public Program getProgram()
	{
		return program;
	}
	public void setProgram(Program v)
	{
		this.program= v;
	}
}