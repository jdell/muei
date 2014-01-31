package com.mxply.muei.apm;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String html = "<html><head><title></title></head><body><div><p style='text-align: center;'><span style='font-size:14px;'><span style='font-family:tahoma,geneva,sans-serif;'><span style='color:#696969;'><strong>MUEI - APM</strong></span></span></span></p><div><p style='text-align: justify;'><span style='font-size:12px;'><span style='font-family:tahoma,geneva,sans-serif;'><strong>Autor</strong>: W. Joel Castro<br/><strong>Email</strong>: wilton.castro@udc.es - joemismito@gmail.com</span></span></p><p style='text-align: justify;'></p></div><p style='text-align: center;'><strong style='color: rgb(169, 169, 169);font-family: tahoma, geneva, sans-serif;font-size: 12px;'>Facultad de Inform&aacute;tica de A Coru&ntilde;a - 2013/2014</strong></p></div><p>&nbsp;</p><p><span _fck_bookmark='1' style='display: none;'>&nbsp;</span><br/><span _fck_bookmark='1' style='display: none;'>&nbsp;</span></p></body></html>";

		WebView v = new WebView(this);
		v.loadData(html, "text/html", null);	
		setContentView(v);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}