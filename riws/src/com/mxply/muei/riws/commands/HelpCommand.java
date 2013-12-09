package com.mxply.muei.riws.commands;

public class HelpCommand extends Command {

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
		sb.append("Command list:\n");
		for(String p:params)
			sb.append(String.format("%s\n", p));
		
		System.out.println(sb.toString());
		
	}

}
