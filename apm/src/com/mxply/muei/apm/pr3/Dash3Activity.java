package com.mxply.muei.apm.pr3;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.mxply.muei.apm.AboutActivity;
import com.mxply.muei.apm.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.location.Location;
import android.location.LocationListener;

public class Dash3Activity extends FragmentActivity {
	 private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	    
	ArrayList<LocationResult> results = null;
	LocationResultArrayAdapter adapter =null; 
	
	RadioGroup rbgMetodo;
  	RadioButton rbDirecta;  	
  	ListView lvResults;	  
	Button btnUpdate;
	Button btnMap;
	Geolocation geolocation = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			    
		setContentView(R.layout.activity_dash3);
		
		rbgMetodo = (RadioGroup)findViewById(R.id.rbgMethod);
		//rbgMetodo.OnCheckedChangeListener();
		
		rbDirecta = (RadioButton) findViewById(R.id.rbDirecta);
		lvResults = (ListView)findViewById(R.id.lvResults);
		results = new ArrayList<LocationResult>();
		adapter = new LocationResultArrayAdapter(this, results);
		lvResults.setAdapter(adapter);
		lvResults.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				  final LocationResult item = (LocationResult) parent.getItemAtPosition(position);
				  LaunchMap(item.getLocation());
			  }
			});
		
		btnUpdate =(Button) findViewById(R.id.btnUpdate);
		btnUpdate.setOnClickListener(new View.OnClickListener()
			{
			    public void onClick(View v)
			    {
			    	if (rbDirecta.isChecked())
			    		geolocation  =new DirectGeolocation(Dash3Activity.this);
			    	else
			    		geolocation  =new InverseGeolocation(Dash3Activity.this);
			    	(new Dash3Activity.GetGeolocationTask()).execute(geolocation);
			    }
			});
	}
	private void LaunchMap(Location location)
	{
		  Uri geoLocation = Uri.parse(String.format("geo:%s,%s", location.getLatitude(), location.getLongitude()));
		  Intent intent = new Intent(Intent.ACTION_VIEW);
		  intent.setData(geoLocation);
		  if (intent.resolveActivity(getPackageManager()) != null) {
			  startActivity(intent);
		  }
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
					Intent intent = new Intent(Dash3Activity.this, AboutActivity.class);
					startActivity(intent);
					break;
				default:
					res =super.onOptionsItemSelected(item);
			}
		   return res;
		   
		}
	 
	 // AsyncTask
	 protected class GetGeolocationTask extends AsyncTask<Geolocation, Void, LocationResult>{

		@Override
		protected LocationResult doInBackground(Geolocation... params) {
			// TODO Auto-generated method stub
			Geolocation gl = params[0];
			gl.updateLocation();
			LocationResult res = new LocationResult();
			res.setLocation(gl.getLocation());
			res.setMethod(gl.toString());
			return res;
		}
		@Override
		protected void onPostExecute(LocationResult result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			results.add(result);
			
			((LocationResultArrayAdapter)lvResults.getAdapter()).notifyDataSetChanged();
		}
		 
	 }
	 
	 // Utils
	 public abstract class Geolocation
	 {
		 protected Context context = null;
		 protected Location currentLocation = null;
		 public abstract boolean updateLocation();
		 public Location getLocation(){
			 return currentLocation;
		 }
		 public Geolocation(Context context)
		 {
			 this.context= context;
		 }
		 protected Location testData()
		 {
			 Location res = new Location("flp");
			 res.setLatitude(37.377166);
			 res.setLongitude(-122.086966);
			 res.setAccuracy(3.0f);
			 
			 return res;
		 }
	 }
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	 public class InverseGeolocation extends Geolocation implements
     LocationListener,
     GooglePlayServicesClient.ConnectionCallbacks,
     GooglePlayServicesClient.OnConnectionFailedListener {

	    // A request to connect to Location Services
	    private LocationRequest mLocationRequest;

	    // Stores the current instantiation of the location client in this object
	    private LocationClient mLocationClient;
	    
		 public InverseGeolocation(Context context)
		 {
			 super(context);
			 
	        // Create a new global location parameters object
	        mLocationRequest = LocationRequest.create();

	        /*
	         * Set the update interval
	         */
	        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

	        // Use high accuracy
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	        // Set the interval ceiling to one minute
	        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

	        mLocationClient = new LocationClient(this.context, this, this);
		 }
		@Override
		public boolean updateLocation() {

	        // If Google Play Services is available
	        if (servicesConnected()) {

	            // Get the current location
	        	currentLocation= mLocationClient.getLastLocation();
	        }
	        else
	        	currentLocation= testData();
	        
			return true;
		}		
	    private boolean servicesConnected() {

	        // Check that Google Play services is available
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.context);

	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) {
	            // In debug mode, log the status
	            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

	            // Continue
	            return true;
	        // Google Play services was not available for some reason
	        } else {
	            // Display an error dialog
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)this.context, 0);
	            if (dialog != null) {
	                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
	                errorFragment.setDialog(dialog);
	                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
	            }
	            return false;
	        }
	    }
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "DIR";
		}
		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) {
	        /*
	         * Google Play services can resolve some errors it detects.
	         * If the error has a resolution, try sending an Intent to
	         * start a Google Play services activity that can resolve
	         * error.
	         */
	        if (connectionResult.hasResolution()) {
	            try {

	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(
	                        (Activity)this.context,
	                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

	                /*
	                * Thrown if Google Play services canceled the original
	                * PendingIntent
	                */

	            } catch (IntentSender.SendIntentException e) {

	                // Log the error
	                e.printStackTrace();
	            }
	        } else {

	            // If no resolution is available, display a dialog to the user with the error.
	            showErrorDialog(connectionResult.getErrorCode());
	        }
			
		}
	    private void showErrorDialog(int errorCode) {

	        // Get the error dialog from Google Play services
	        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
	            errorCode,
	            (Activity)this.context,
	            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

	        // If Google Play services can provide an error dialog
	        if (errorDialog != null) {

	            // Create a new DialogFragment in which to show the error dialog
	            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

	            // Set the dialog in the DialogFragment
	            errorFragment.setDialog(errorDialog);

	            // Show the error dialog in the DialogFragment
	            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
	        }
	    }
		@Override
		public void onConnected(Bundle arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
	 }
	 public class DirectGeolocation extends Geolocation{
		String address = null;

		 public DirectGeolocation(Context context)
		 {
			 super(context);
		 }
		
		public String getAddressCode() {
			return address;
		}

		public void setZipCode(String address) {
			this.address = address;
		}


		@Override
		public boolean updateLocation() {
			// TODO Auto-generated method stub
			currentLocation= testData();
			
			return true;
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "I: " + address;
		}
		 
	 }
}

