package com.mxply.muei.riws.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Command {

	private String _logfile = null;
	private StringBuilder _sb = new StringBuilder();
	protected void log(String message)
	{
		log(message, true);
	}
	protected void log(String message, Boolean screen)
	{
		try {
			_sb.append(message).append("\n");
			if (screen) System.out.println(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	protected void logcommit()
	{
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(_logfile, true)));
			out.println(_sb.toString());
			out.close();
			
			_sb = new StringBuilder();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void execute(String params[]){
		_logfile = String.format("%s-%s.log",  getKey(), new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(new Date()));
		if (canExecute(params))
		{
			action(params);
		}
		else
		{
			System.out.println("Incorrect format.");
			System.out.println(toString());
		}
	}
	public String toString() {
		return String.format("%s %s", this.getKey(), this.getParams());
	}
	
	public abstract Boolean canExecute(String params[]);
	public abstract String getKey();
	protected abstract String getParams();
	protected abstract void action(String params[]);
}
