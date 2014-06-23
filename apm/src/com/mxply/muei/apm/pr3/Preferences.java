package com.mxply.muei.apm.pr3;

import android.app.Activity;
import android.os.Bundle;

public class Preferences extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new OpcionesFragment()).commit();
	}
}
