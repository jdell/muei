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
	
	public RIWSEngine(){		
		_helpCommand =new HelpCommand();
		_commands = new ArrayList<Command>();
		_commands.add(new IndexingCommand());
		_commands.add(new ReadingCommand());
		_commands.add(new SearchingCommand());
		_commands.add(new TrecCommand());
		_commands.add(new MapCommand());
		_commands.add(_helpCommand);
		_commands.add(this);				
	}
	public void start()
	{					
		Hashtable<String, Command> dict = new Hashtable<String, Command>();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < _commands.size(); i++)
		{
			dict.put(_commands.get(i).getKey(), _commands.get(i));	
			list.add(_commands.get(i).toString());
		}
		String[] helpparams = list.toArray(new String[list.size()]);
		

		Scanner scanner = new Scanner(System.in);

		_running = true;
		String _cmd = null;
		String _args[] = null;
		_helpCommand.execute(helpparams);
		while(_running){			
			_cmd = scanner.nextLine();
			
			_args = _cmd.split(" ");
			if ((_helpCommand.getKey().equals(_args[0])) || (!dict.containsKey(_args[0])))
				_helpCommand.execute(helpparams);
			else
			{
				String params[] = new String[_args.length-1];
				System.arraycopy(_args, 1, params, 0, params.length);
				dict.get(_args[0]).execute(params);
			}
			
		};
		scanner.close();
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
