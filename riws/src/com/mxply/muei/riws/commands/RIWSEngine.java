package com.mxply.muei.riws.commands;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class RIWSEngine extends Command {

	Boolean _running =false;
	HelpCommand _helpCommand = null;
	List<Command> _commands = null;
	String _prompt = "riws$ ";
	
	public RIWSEngine(){			
		_mustwatch = false;	
		_helpCommand =new HelpCommand();
		_commands = new ArrayList<Command>();
		_commands.add(new IndexingCommand());
		_commands.add(new ReadingCommand());
		_commands.add(new SearchingCommand());
		_commands.add(new TrecCommand());
		_commands.add(new MapCommand());
		_commands.add(new ClearCommand());
		_commands.add(_helpCommand);
		_commands.add(this);				
	}
	public void start(String args[])
	{					
		Hashtable<String, Command> dict = new Hashtable<String, Command>();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < _commands.size(); i++)
		{
			dict.put(_commands.get(i).getKey(), _commands.get(i));	
			list.add(_commands.get(i).toString());
		}
		String[] helpparams = list.toArray(new String[list.size()]);
		
		if (args!=null && args.length>0)
		{
			System.out.println();
			System.out.print(_prompt);
			System.out.println(com.mxply.muei.riws.common.parser.join(args, " ", 0));
			processCmd(args, dict, helpparams);
		}else
			_helpCommand.execute(helpparams);

		Scanner scanner = new Scanner(System.in);

		_running = true;
		String _cmd = null;
		String _args[] = null;
		while(_running){	
			System.out.println();
			System.out.print(_prompt);
			_cmd = scanner.nextLine();
			
			_args = _cmd.split(" ");
			
			processCmd(_args, dict, helpparams);
		};
		scanner.close();
	}
	private void processCmd(String args[], Hashtable<String, Command> dict, String[] helpparams)
	{
		if ((_helpCommand.getKey().equals(args[0])) || (!dict.containsKey(args[0])))
			_helpCommand.execute(helpparams);
		else
		{
			String params[] = new String[args.length-1];
			System.arraycopy(args, 1, params, 0, params.length);
			dict.get(args[0]).execute(params);
		}
	}
	
	@Override
	public Boolean canExecute(String[] params) {
		return true;
	}

	@Override
	public String getKey() {
		return "quit";
	}

	@Override
	protected void action(String[] params) {
		_running = false;		
	}

	@Override
	protected String getParams() {
		return "";
	}

}
