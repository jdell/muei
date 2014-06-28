package es.udc.fic.muei.apm.geolocalizacion.fragments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import es.udc.fic.muei.apm.geolocalizacion.LocationMainActivity;
import es.udc.fic.muei.apm.geolocalizacion.R;
import es.udc.fic.muei.apm.geolocalizacion.common.LocationUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ReverseLocationFragment extends Fragment implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
    
    private Activity activity;
    
    // Handles to UI widgets
    private TextView txtLatLng;
    private TextView txtAddress;
    private Button btnGetAddress;
    private Button btnViewMap;

    private TextView txtConnectionState;
    private TextView txtConnectionStatus;
    private Button btnStartUpdates;
    private Button btnStopUpdates;

    private ProgressBar mActivityIndicator;

    // Handle to SharedPreferences for this app
    SharedPreferences mPrefs;

    // Handle to a SharedPreferences editor
    SharedPreferences.Editor mEditor;

    /*
     * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
     * method handleRequestSuccess of LocationUpdateReceiver.
     *
     */
    boolean mUpdatesRequested = false;

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    private Boolean saveXML = false;
    
    private Boolean startXml = false; 
    
    private FileOutputStream fout = null;   	
	 
    private XmlSerializer serializer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
   	 	 activity = this.getActivity();
	 
    	 View rootView = inflater.inflate(R.layout.fragment_reverse_location, container, false);
    	 txtLatLng = (TextView)rootView.findViewById(R.id.txtLatLng);
    	 txtAddress = (TextView)rootView.findViewById(R.id.txtAddress);
    	 btnGetAddress = (Button)rootView.findViewById(R.id.btnGetAddress);
    	 btnGetAddress.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {

	    		// If Google Play Services is available
	            if (((LocationMainActivity)activity).servicesConnected()) {
	            	String method = mPrefs.getString("settings_location_method_title","geo");

			    	if (method.equals("http")){
		            	// Turn the indefinite activity indicator on
		                mActivityIndicator.setVisibility(View.VISIBLE);

		            	 // Get the current location
		                Location currentLocation = mLocationClient.getLastLocation();
		                
		            	 // Start the background task
		                (new GetAddressAlternativeTask(activity)).execute(currentLocation);
			    	}else{
			    		 // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
			            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
			                // No geocoder is present. Issue an error message
			                Toast.makeText(activity, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
			                return;
			            }
		                // Get the current location
		                Location currentLocation = mLocationClient.getLastLocation();

		                // Turn the indefinite activity indicator on
		                mActivityIndicator.setVisibility(View.VISIBLE);

		                // Start the background task
		                (new GetAddressTask(activity)).execute(currentLocation);
			    	}	
	            }	  
			}
		});
    	 btnViewMap = (Button)rootView.findViewById(R.id.btnViewMap);
    	 btnViewMap.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	String latLng = txtLatLng.getText().toString();      
		        if (latLng!=null && latLng.length()>0){
	            	String[] aLatLng = latLng.split(",");

	            	double latitude = Double.parseDouble(aLatLng[0]);
	            	double longitude = Double.parseDouble(aLatLng[1]);        	
	            	String uriBegin = "geo:" + latitude + "," + longitude;
	            	String query = latitude + "," + longitude;
	            	String encodedQuery = Uri.encode(query);
	            	String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
	            	Uri uri = Uri.parse(uriString);
	            	
		            ((LocationMainActivity)activity).launchMapIntent(uri);
		        }else{
			          Toast.makeText(activity, "No lat/lng specified", Toast.LENGTH_SHORT).show();
		        }					
			}
		});
         mActivityIndicator = (ProgressBar) rootView.findViewById(R.id.address_progress);
         txtConnectionState = (TextView) rootView.findViewById(R.id.text_connection_state);
         txtConnectionStatus = (TextView) rootView.findViewById(R.id.text_connection_status);
    	 btnStartUpdates = (Button)rootView.findViewById(R.id.start_updates);
    	 btnStartUpdates.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        mUpdatesRequested = true;

		        if (((LocationMainActivity)activity).servicesConnected()) {
		            startPeriodicUpdates();
		        }
				
			}
		});
    	 btnStopUpdates = (Button)rootView.findViewById(R.id.stop_updates);
    	 btnStopUpdates.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        mUpdatesRequested = false;

		        if (((LocationMainActivity)activity).servicesConnected()) {
		            stopPeriodicUpdates();
		        }
				
			}
		});

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

         // Note that location updates are off until the user turns them on
         mUpdatesRequested = false;

         mPrefs = PreferenceManager.getDefaultSharedPreferences(this.activity);
         
         // Get an editor
         mEditor = mPrefs.edit();

         /*
          * Create a new location client, using the enclosing class to
          * handle callbacks.
          */
         mLocationClient = new LocationClient(activity, this, this);
         
    	 return rootView;
    }
    
    @Override
    public void onResume() {
    	super.onResume();

        // If the app already has a setting for getting location updates, get it
        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

        // Otherwise, turn off location updates until requested
        } else {
            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            mEditor.commit();
        }
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
                        this.activity,
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
            ((LocationMainActivity)activity).showErrorDialog(connectionResult.getErrorCode());
        }
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        txtConnectionStatus.setText(R.string.connected);

        if (mUpdatesRequested) {
            startPeriodicUpdates();
        }
		
	}

	@Override
	public void onDisconnected() {
        txtConnectionStatus.setText(R.string.disconnected);
	}

	@Override
	public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        txtConnectionStatus.setText(R.string.location_updated);

        // In the UI, set the latitude and longitude to the value received
        String latLng = LocationUtils.getLatLng(this.activity, location);
        txtLatLng.setText(latLng);
        
        if(saveXML){
        	updatesXML();
        }
		
	}
    
	@Override
	public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();
	}
	@Override
	public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();
        
        finXML();

        super.onStop();
	}
	@Override
	public void onPause() {

        // Save the current setting for updates
        mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
        mEditor.commit();
        
        finXML();

        super.onPause();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

                        // Display the result
                        txtConnectionState.setText(R.string.connected);
                        txtConnectionStatus.setText(R.string.resolved);
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                        // Display the result
                        txtConnectionState.setText(R.string.disconnected);
                        txtConnectionStatus.setText(R.string.no_resolution);

                    break;
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(LocationUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
	}

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        txtConnectionState.setText(R.string.location_requested);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
        txtConnectionState.setText(R.string.location_updates_stopped);
    }
    private void finXML() {
    	startXml = false;
    	try {     
  	      serializer.endDocument();    	
  	      serializer.flush();    	
  	      fout.close();  	
  	     	    	
  	   } catch (Exception e) {    
       	e.printStackTrace();
  	   }
    }
    private void updatesXML() {
    	
    	if(!startXml){
    		startXml = true;
    		startXML();
    	}
    	// Get the current location
        Location currentLocation = mLocationClient.getLastLocation();
        
        String locationSave = LocationUtils.getLatLng(this.activity, currentLocation);
        try {
        	serializer.startTag(null, "location");       	
    	
        	serializer.text(locationSave);    
	      
        	serializer.endTag(null, "location"); 
        } catch (Exception e) {    
        	e.printStackTrace();
        }	
    }
    
    private void startXML() {   	
    	      	
    	   try {    	
    	      fout = this.activity.openFileOutput("location.xml", Activity.MODE_PRIVATE);    	
    	   } catch (FileNotFoundException e) {    	
    	      Toast.makeText(this.activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    	   }

    	   serializer = Xml.newSerializer();
    	
    	   try {
    	
    	      serializer.setOutput(fout, "UTF-8");    	
    	      serializer.startDocument(null, true);    	
    	      serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
   	
    	   } catch (Exception e) {    
    	      Toast.makeText(this.activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    	   }
    	
    	}
    protected class GetAddressTask extends AsyncTask<Location, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Location... params) {
            /*
             * Get a new geocoding service instance, set for localized addresses. This example uses
             * android.location.Geocoder, but other geocoders that conform to address standards
             * can also be used.
             */
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

            // Get the current location from the input parameter list
            Location location = params[0];

            // Create a list to contain the result address
            List <Address> addresses = null;

            // Try to get an address for the current location. Catch IO or network problems.
            try {

                /*
                 * Call the synchronous getFromLocation() method with the latitude and
                 * longitude of the current location. Return at most 1 address.
                 */
                addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1
                );

                // Catch network or other I/O problems.
                } catch (IOException exception1) {

                    // Log an error and return an error message
                    Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

                    // print the stack trace
                    exception1.printStackTrace();

                    // Return an error message
                    return (getString(R.string.IO_Exception_getFromLocation));

                // Catch incorrect latitude or longitude values
                } catch (IllegalArgumentException exception2) {

                    // Construct a message containing the invalid arguments
                    String errorString = getString(
                            R.string.illegal_argument_exception,
                            location.getLatitude(),
                            location.getLongitude()
                    );
                    // Log the error and print the stack trace
                    Log.e(LocationUtils.APPTAG, errorString);
                    exception2.printStackTrace();

                    //
                    return errorString;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {

                    // Get the first address
                    Address address = addresses.get(0);

                    // Format the first line of address
                    String addressText = getString(R.string.address_output_string,

                            // If there's a street address, add it
                            address.getMaxAddressLineIndex() > 0 ?
                                    address.getAddressLine(0) : "",

                            // Locality is usually a city
                            address.getLocality(),

                            // The country of the address
                            address.getCountryName()
                    );

                    // Return the text
                    return addressText;

                // If there aren't any addresses, post a message
                } else {
                  return getString(R.string.no_address_found);
                }
        }

        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(String address) {

            // Turn off the progress bar
            mActivityIndicator.setVisibility(View.GONE);

            // Set the address in the UI
            txtAddress.setText(address);
            
           ((LocationMainActivity)activity).setShareIntent(address);
        }
    }
    protected class GetAddressAlternativeTask extends AsyncTask<Location, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressAlternativeTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Location... params) {
           
        	try {
        			      
        		// Get the current location from the input parameter list
                Location currentLocation = params[0];
        		
	        	StringBuilder sbURL = new StringBuilder();        	
	        	sbURL.append("http://maps.google.com/maps/api/geocode/json?latlng=");
	        	sbURL.append(currentLocation.getLatitude() + "," +currentLocation.getLongitude());
	        	sbURL.append("&sensor=false");
	        	
	        	HttpGet httpGet = new HttpGet(sbURL.toString());
	        	HttpClient client = new DefaultHttpClient();
	        	HttpResponse response;
	        	StringBuilder sbr = new StringBuilder();
	        	
	        	response = client.execute(httpGet);
	        	HttpEntity entity = response.getEntity();
	        	InputStream stream = entity.getContent();
	        	int b;
	        	while ((b = stream.read())!=-1) {
					sbr.append((char) b);				
				}
	        	
	        	JSONObject jsonObject = new JSONObject(sbr.toString());
	        	
	        	String name = new String(((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address").getBytes("ISO-8859-1"),"UTF-8");
	        		        	
	            // Display the current location in the UI
	           return (name);	
             

             // Catch network or other I/O problems.
            } catch (Exception exception) {

                    // Log an error and return an error message
                    Log.e(LocationUtils.APPTAG, exception.getMessage());

                    // print the stack trace
                    exception.printStackTrace();

                    // Return an error message
                    return (exception.getMessage());               
             } 
                
        }
        
        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
		@Override
        protected void onPostExecute(String address) {
        	
        	// Turn off the progress bar
            mActivityIndicator.setVisibility(View.GONE);
            
            // Set the address in the UI
            txtAddress.setText(address);
            
            ((LocationMainActivity)activity).setShareIntent(address);
        }
    }
    
}
