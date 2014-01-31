package com.mxply.muei.apm.pr0;

import com.mxply.muei.apm.MainActivity;
import com.mxply.muei.apm.R;
import com.mxply.muei.apm.R.id;
import com.mxply.muei.apm.R.layout;
import com.mxply.muei.apm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		// Get the message from the intent
		setContentView(R.layout.activity_display_message);
	    	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}
}
