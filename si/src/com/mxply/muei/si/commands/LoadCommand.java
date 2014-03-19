package com.mxply.muei.si.commands;

import java.io.File;
import java.io.IOException;

import com.mxply.muei.si.Environment;
import com.mxply.muei.si.common.*;

public class LoadCommand extends Command  {

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length>0;
	}

	@Override
	public String getKey() {
		return "load";
	}

	@Override
	protected String getParams() {
		return "filename";
	}

	@Override
	protected void action(String[] params) {
		try
		{
			if (params==null || params.length==0) 
			{
				System.out.println("Debe especificar el fichero de entrada");
				return;
			}
			
			String ipath = Environment.Current().getFullPath(params[0]);

			File ifile = new File(ipath);
			if (ifile.exists() && ifile.isFile())
			{			
				Environment.Current().setProgram(Program.loadFrom(ifile));
			}
			else
			{
				System.out.format("Fichero no encontrado [%s ]. Directorio actual: %s\n", ipath, new File(Environment.Current().getDirectory()).getCanonicalPath());
			}

		}catch(IOException ex)
		{
			ex.printStackTrace();
		}		
	}

}
