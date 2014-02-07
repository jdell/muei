/*
 * MUEI - APM - 2013/2014
 * Autores: 
 * 		Angel Rico Diaz (angel.rico@udc.es)
 * 		W. Joel Castro (wilton.castro@udc.es) 
 * */
package com.mxply.muei.apm.pr2;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import com.mxply.muei.apm.AboutActivity;
import com.mxply.muei.apm.MainActivity;
import com.mxply.muei.apm.R;
import com.mxply.muei.apm.R.layout;
import com.mxply.muei.apm.R.menu;

import android.os.Bundle;
import android.renderscript.Type;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Dash2Activity extends Activity {

	private boolean isConnected = false;
	
	private SensorManager mSensorManager;

	private Sensor sAcelerometro;
	private Sensor sGiroscopio;
	private Sensor sMagnetometro;

	private SensorEventListener listener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			
			int type =event.sensor.getType();
			StringBuilder sb = new StringBuilder();
			DecimalFormat dformat = new DecimalFormat("#0.00");
			TextView txt = null;

			
			sb.append(dformat.format(event.values[0]));
			sb.append(" - ");
			sb.append(dformat.format(event.values[1]));
			sb.append(" - ");
			sb.append(dformat.format(event.values[2]));
			
			//sb.append("TESTING");
			switch(type)
			{
			case Sensor.TYPE_ACCELEROMETER:
			{
				txt =(TextView) findViewById(R.id.txtAcelerometro);					
			}
				break;
			case Sensor.TYPE_GYROSCOPE:
			{
				txt =(TextView) findViewById(R.id.txtGiroscopio);
			}
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
			{
				txt =(TextView) findViewById(R.id.txtMagnetometro);
			}
				break;
				default:
			}
			if (txt!=null)
				txt.setText(sb.toString());
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash2);

		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		StringBuilder sb = new StringBuilder();
		for (Sensor sensor : mSensorManager.getSensorList(Sensor.TYPE_ALL)) {
			sb.append(" - ").append(sensor.getName()).append('\n');
		}
		TextView txt = (TextView)findViewById(R.id.txtSensores);
		txt.setText(sb.toString());

		PackageManager PM= this.getPackageManager();
		sAcelerometro = (PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER))?mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER):null;
		sGiroscopio = (PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE))?mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE):null;				
		sMagnetometro = (PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS))?mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD):null;

		Button btn = (Button)findViewById(R.id.btnConnect);
		btn.setOnClickListener(new View.OnClickListener()
		{
		    public void onClick(View v)
		    {	
		    	connect();		    
		    }
		});
		btn = (Button)findViewById(R.id.btnDisconnect);
		btn.setOnClickListener(new View.OnClickListener()
		{
		    public void onClick(View v)
		    {	
	    		disconnect();			    		    
		    }
		});
	}
	private void switchButtonStatus()
	{
		isConnected = !isConnected;
		
		Button btnConnect = (Button)findViewById(R.id.btnConnect);
		Button btnDisconnect = (Button)findViewById(R.id.btnDisconnect);

		btnConnect.setEnabled(!isConnected);
		btnDisconnect.setEnabled(isConnected);
	}
private void connect()
{
	if (!isConnected)
	{				
		if (sAcelerometro!=null) mSensorManager.registerListener(listener, sAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
		if (sMagnetometro!=null) mSensorManager.registerListener(listener, sMagnetometro, SensorManager.SENSOR_DELAY_NORMAL);
		if (sGiroscopio!=null) mSensorManager.registerListener(listener, sGiroscopio, SensorManager.SENSOR_DELAY_NORMAL);

		switchButtonStatus();
	}
}
private void disconnect()
{
	if (isConnected)
	{	
		mSensorManager.unregisterListener(listener);	

		switchButtonStatus();
	}
}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		connect();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		disconnect();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		disconnect();
		
		super.onStop();
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
