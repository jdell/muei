package com.mxply.muei.apm.pr1;

import android.os.Bundle;
import android.app.Activity;

public class Dash1SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 getFragmentManager().beginTransaction()
         .replace(android.R.id.content, new Dash1SettingsFragment())
         .commit();
	}


}
