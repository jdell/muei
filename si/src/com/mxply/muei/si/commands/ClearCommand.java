package com.mxply.muei.si.commands;

public class ClearCommand extends Command {

	public ClearCommand()
	{
		_mustwatch = false;
	}
	@Override
	public Boolean canExecute(String[] params) {
		return true;
	}

	@Override
	public String getKey() {
		return "clear";
	}

	@Override
	protected String getParams() {
		return "";
	}

	@Override
	protected void action(String[] params) {
		try
	    {
	        String os = System.getProperty("os.name");

	        if (os.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (Exception exception)
	    {
	    	for (int i = 0; i < 50; ++i) System.out.println();
	    }
	}

}
