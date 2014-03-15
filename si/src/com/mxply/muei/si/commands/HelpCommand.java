package com.mxply.muei.si.commands;

public class HelpCommand extends Command {

	public HelpCommand()
	{
		_mustwatch = false;
	}
	@Override
	public Boolean canExecute(String[] params) {
		return true;
	}

	@Override
	public String getKey() {
		return "help";
	}

	@Override
	protected String getParams() {
		return "";
	}

	@Override
	protected void action(String[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append("**************** List of Commands **************** \n");
		for(String p:params)
			sb.append(String.format("\n%s", p));
		
		System.out.println(sb.toString());
		
	}

}