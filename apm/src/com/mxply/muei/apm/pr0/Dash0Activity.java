package com.mxply.muei.apm.pr0;

import com.mxply.muei.apm.R;
import com.mxply.muei.apm.R.layout;
import com.mxply.muei.apm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Dash0Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dash0, menu);
		return true;
	}

}
