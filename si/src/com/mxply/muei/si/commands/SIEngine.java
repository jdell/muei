package com.mxply.muei.si.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import com.mxply.muei.si.Environment;

public class SIEngine extends Command {

	Boolean _running =false;
	HelpCommand _helpCommand = null;
	List<Command> _commands = null;
	String _baseprompt = "si$ ";

	
	public SIEngine(){			
		_mustwatch = false;	
		_helpCommand =new HelpCommand();
		_commands = new ArrayList<Command>();
		_commands.add(new LoadCommand());
		_commands.add(new ReloadCommand());
		_commands.add(new StableCommand());
		_commands.add(new TopDownCommand());
		_commands.add(new BottomUpCommand());
		_commands.add(new ChangeDirectoryCommand());
		_commands.add(new PrintCommand());
		_commands.add(new ClearCommand());
		_commands.add(_helpCommand);
		_commands.add(this);				
	}

	public String getPrompt()
	{
		return String.format("%s/%s", Environment.Current().getDirectory(), _baseprompt);
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
			System.out.print(getPrompt());
			if (args!=null)
				System.out.println(com.mxply.muei.si.common.parser.join(args, " ", 0));
			processCmd(args, dict, helpparams);
		}else
			_helpCommand.execute(helpparams);

		Scanner scanner = new Scanner(System.in);

		_running = true;
		String _cmd = null;
		String _args[] = null;
		while(_running){	
			System.out.println();
			System.out.print(getPrompt());
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
/*
		try
		{
			if (args==null || args.length==0) 
			{
				System.out.println("Debe especificar el fichero de entrada");
				return;
			}
			
			String ipath = args[0];

			File ifile = new File(ipath);
			if (ifile.exists() && ifile.isFile())
			{			
				Program program = Program.loadFrom(ifile);
				List<Algorithm> algs = new ArrayList<Algorithm>();
				algs.add(new IterateStableAlgorithm());
				
				for (Algorithm algorithm : algs) {
					algorithm.execute(program);
				}
			}
			else
			{
				System.out.format("Fichero no encontrado [%s ]. Directorio actual: %s\n", ipath, new File(".").getCanonicalPath());
			}

		}catch(IOException ex)
		{
			ex.printStackTrace();
		}		

	*/
}
