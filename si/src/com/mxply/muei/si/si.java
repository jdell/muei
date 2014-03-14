package com.mxply.muei.si;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mxply.muei.si.algorithms.*;
import com.mxply.muei.si.common.Program;

public class si {

	public static void main(String[] args) {
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
	}

}
