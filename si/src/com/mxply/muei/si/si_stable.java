package com.mxply.muei.si;

import java.io.File;
import java.io.IOException;

import com.mxply.muei.si.algorithms.IterateStableAlgorithm;
import com.mxply.muei.si.common.Program;

public class si_stable {

	public static void main(String[] params) throws IOException {
		//1. Cargo el programa
		if (params==null || params.length==0) 
		{
			System.out.println("Debe especificar el fichero de entrada");
			return;
		}
		
		String ipath = params[0];

		File ifile = new File(ipath);
		if (ifile.exists() && ifile.isFile())
		{			
			Environment.Current().setProgram(Program.loadFrom(ifile));
		}
		else
		{
			System.out.format("Fichero no encontrado [%s ]. Directorio actual: %s\n", ipath, new File(Environment.Current().getDirectory()).getCanonicalPath());
		}
		
		//2. Ejecuto el programa
		IterateStableAlgorithm algorithm = new IterateStableAlgorithm();
		algorithm.execute(Environment.Current().getProgram());
		
	}

}
