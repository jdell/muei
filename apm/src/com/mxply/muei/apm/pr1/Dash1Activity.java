package com.mxply.muei.apm.pr1;

import com.mxply.muei.apm.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class Dash1Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_dash1);
		
		//http://www.willpeavy.com/minifier/
		String html = "<html><head><title></title></head><body><div><strong>Pr&aacute;ctica a entregar</strong></div><ul><li>Desarrollar en proyecto Hello World o nuevo proyecto<ul><li>Enlazar opci&oacute;n &ldquo;Settings&rdquo;del menu, para que haga un intent de una nueva activity &ldquo;Preferencias&rdquo;</li><li>http://developer.android.com/guide/topics/ui/menus.html</li></ul></li></ul><ul><li>Desarrollar la nueva activity &ldquo;Preferences&rdquo;utilizando el API<ul><li>http://developer.android.com/guide/topics/ui/settings.html</li><li>Usando la versi&oacute;n con PreferenceFragment (La versi&oacute;n sin uso de Fragments est&aacute;obsoleta)</li><li>Considerar los siguientes tipos:<ul><li>CheckBoxPreference</li><li>EditTextPreference</li><li>ListPreference (opcional) (Necesario el uso de string-arrays)</li></ul></li></ul></li></ul></body></html>";

		WebView v = new WebView(this);
		v.loadData(html, "text/html", null);	
		setContentView(v);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dash1, menu);
		return true;
	}
	
 @Override
	public boolean onOptionsItemSelected(MenuItem item){
	 	boolean res = true;
		switch (item.getItemId())
		{
			case R.id.action_settings:
				Intent intent = new Intent(Dash1Activity.this, Dash1SettingsActivity.class);
				startActivity(intent);
				break;
			default:
				res =super.onOptionsItemSelected(item);
		}
	   return res;
	   
	}
}
