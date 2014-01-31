package com.mxply.muei.apm;

import com.mxply.muei.apm.pr0.Dash0Activity;
import com.mxply.muei.apm.pr1.Dash1Activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		initializeLayout();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//**************
	public enum tButton
	{
		PR01(0, Dash0Activity.class, false),
		PR02(1, Dash1Activity.class);

	    private final int id;
	    private final boolean enabled;
	    private final Class<?> cls;
	    
	    tButton(int id, Class<?> cls) { this(id, cls, true); }
	    tButton(int id, Class<?> cls, boolean enabled) { this.id = id; this.cls=cls; this.enabled=enabled; }
	    public int getValue() { return id; }
	    public boolean isEnabled() { return enabled; }
	    public Class<?> getCLS() { return cls; }
	}
	private void initializeLayout()
	{
		String name = (String)getResources().getText(R.string.btn_practice);
		LinearLayout mainlayout = (LinearLayout) findViewById(R.id.main_layout);
		for (tButton b : tButton.values()) {
			Button btn = new Button(this);
			btn.setId(b.getValue());
			btn.setText(name + " " + b.getValue());
			btn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			btn.setOnClickListener(new View.OnClickListener()
			{
			    public void onClick(View v)
			    {
			    	tButton b = tButton.values()[v.getId()];
			    	if (b.isEnabled())
			    	{
			    		Intent intent = new Intent(MainActivity.this, b.getCLS());
			    		startActivity(intent);
			    	}
			    	else{
			    		Toast.makeText(MainActivity.this, getResources().getText(R.string.message_under_construction), Toast.LENGTH_SHORT).show();
			    	}
			    }
			});
			
			mainlayout.addView(btn);
		}
	}

}
