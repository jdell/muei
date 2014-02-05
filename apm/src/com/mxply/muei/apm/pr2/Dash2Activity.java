/*
 * MUEI - APM - 2013/2014
 * Autores: 
 * 		Angel Rico Diaz ()
 * 		W. Joel Castro (wilton.castro@udc.es) 
 * */
package com.mxply.muei.apm.pr2;

import com.mxply.muei.apm.AboutActivity;
import com.mxply.muei.apm.MainActivity;
import com.mxply.muei.apm.R;
import com.mxply.muei.apm.R.layout;
import com.mxply.muei.apm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class Dash2Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 @Override
		public boolean onOptionsItemSelected(MenuItem item){
		 	boolean res = true;
			switch (item.getItemId())
			{
				case R.id.action_about:
					Intent intent = new Intent(Dash2Activity.this, AboutActivity.class);
					startActivity(intent);
					break;
				default:
					res =super.onOptionsItemSelected(item);
			}
		   return res;
		   
		}

}
