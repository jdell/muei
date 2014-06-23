package com.mxply.muei.apm.pr3;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.LocationListener;

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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.mxply.muei.apm.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

public class Dash3Activity extends FragmentActivity implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    // Handles to UI widgets
    private TextView mLatLng;
    private TextView mAddress;
    private ProgressBar mActivityIndicator;
    private TextView mConnectionState;
    private TextView mConnectionStatus;
    private EditText mEditAddress;

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
    
    private ShareActionProvider mShareActionProvider;
    
    private Boolean saveXML = false;
    
    private Boolean startXml = false; 
    
    private FileOutputStream fout = null;   	
	 
    private XmlSerializer serializer = null;
    
    private ContactsManager contacts;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.activity_dash3);
        

        contacts = new ContactsManager(this);

        // Get handles to the UI view objects
        mLatLng = (TextView) findViewById(R.id.lat_lng);
        mAddress = (TextView) findViewById(R.id.address);
        mActivityIndicator = (ProgressBar) findViewById(R.id.address_progress);
        mConnectionState = (TextView) findViewById(R.id.text_connection_state);
        mConnectionStatus = (TextView) findViewById(R.id.text_connection_status);
        mEditAddress = (EditText) findViewById(R.id.editText_adress);

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

        // Open Shared Preferences
        //mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Get an editor
        mEditor = mPrefs.edit();

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
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
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

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
    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
/*TODO: Aqui
    	contacts.onActivityResult(requestCode, resultCode, intent, new onSelectedEmail() {   
            @Override
            public void onSuccess(String address) {
             //Sustituid HolaMundoActivity por el nombre de vuestra Activity
             Toast.makeText(Dash3Activity.this, "Addres " + address, Toast.LENGTH_SHORT).show();
            
             
             mEditAddress.setText(address);
             
             setShareIntent(address);
             
            }
            
            @Override
            public void onFailure() {    
             //Implementar el proceso correspondiente en caso de fallo
            }
           });
    	*/
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
                        mConnectionState.setText(R.string.connected);
                        mConnectionStatus.setText(R.string.resolved);
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                        // Display the result
                        mConnectionState.setText(R.string.disconnected);
                        mConnectionStatus.setText(R.string.no_resolution);

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
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }

    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void getLocation(View v) {    	
    	
    		// If Google Play Services is available
            if (servicesConnected()) {
            	
            	

                // Get the current location
                Location currentLocation = mLocationClient.getLastLocation();

                // Display the current location in the UI
                mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
            }

    }

    /**
     * Invoked by the "Get Address" button.
     * Get the address of the current location, using reverse geocoding. This only works if
     * a geocoding service is available.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    // For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
    public void getAddress(View v) {
    	
    	String method = mPrefs.getString("pref_location_method","U");
    	
    	//si es por http, en caso contrario se hace por geo
    	if(method.equals("H")){
    		// If Google Play Services is available
            if (servicesConnected()) {
            	
            	// Turn the indefinite activity indicator on
                mActivityIndicator.setVisibility(View.VISIBLE);
                
            	 // Get the current location
                Location currentLocation = mLocationClient.getLastLocation();
                
            	 // Start the background task
                (new Dash3Activity.GetAddressAlternativeTask(this)).execute(currentLocation);
                
            }
    		
    	}else{
    		 // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
                // No geocoder is present. Issue an error message
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }

            if (servicesConnected()) {

                // Get the current location
                Location currentLocation = mLocationClient.getLastLocation();

                // Turn the indefinite activity indicator on
                mActivityIndicator.setVisibility(View.VISIBLE);

                // Start the background task
                (new Dash3Activity.GetAddressTask(this)).execute(currentLocation);
            }
    	}

       
    }

    /**
     * Invoked by the "Start Updates" button
     * Sends a request to start location updates
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void startUpdates(View v) {
        mUpdatesRequested = true;

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

    /**
     * Invoked by the "Stop Updates" button
     * Sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates(View v) {
        mUpdatesRequested = false;

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        mConnectionStatus.setText(R.string.connected);

        if (mUpdatesRequested) {
            startPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        mConnectionStatus.setText(R.string.disconnected);
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
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
                        this,
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

    /**
     * Report location updates to the UI.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {

        // Report to the UI that the location was updated
        mConnectionStatus.setText(R.string.location_updated);

        // In the UI, set the latitude and longitude to the value received
        mLatLng.setText(LocationUtils.getLatLng(this, location));
        
        if(saveXML){
        	updatesXML();
        }
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        mConnectionState.setText(R.string.location_requested);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
        mConnectionState.setText(R.string.location_updates_stopped);
    }

    /**
     * An AsyncTask that calls getFromLocation() in the background.
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * String   - An address passed to onPostExecute()
     */
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
            mAddress.setText(address);
            
           setShareIntent(address);
        }
    }

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
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

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    /**
     * An AsyncTask that calls .
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * String   - An address passed to onPostExecute()
     */
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
	        	
	        	//float lon = (float) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
	        	//float lat = (float) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
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
        @SuppressLint("NewApi")
		@Override
        protected void onPostExecute(String latLng) {
        	
        	// Turn off the progress bar
            mActivityIndicator.setVisibility(View.GONE);
            
            // Set the address in the UI
            mAddress.setText(latLng);
            
            setShareIntent(latLng);
        }
    }
       
    
    /**
     * Invoked by the "Get Location Alternative" button.
     *
     * 
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void getLocationAlternative(View v) {

    	 // Get text edit
    	String addr = mEditAddress.getText().toString();      
        
    	 // Start the background task
        (new Dash3Activity.GetLocationAlternativeTask(this)).execute(addr);
    }
    
    /**
     * An AsyncTask that calls .
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * String   - An address passed to onPostExecute()
     */
    protected class GetLocationAlternativeTask extends AsyncTask<String, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetLocationAlternativeTask(Context context) {

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
        protected String doInBackground(String... params) {
           
        	try {
        			      
        		// Get the current location from the input parameter list
                String addr = params[0];
                addr= addr.replace("\n", "").replace(" ", "%20");
        		
	        	StringBuilder sbURL = new StringBuilder();        	
	        	sbURL.append("http://maps.google.com/maps/api/geocode/json?address=");
	        	sbURL.append(addr);
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
	        	
	        	float lon = (float) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
	        	float lat = (float) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
	        	//String name = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
	        	
	        	
	            // Display the current location in the UI
	           return (""+ lat + ", " +lon);	
             

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
        protected void onPostExecute(String latLng) {        
            
            // Set the address in the UI
            mLatLng.setText(latLng);
        }
    }
    
    /**
     * Invoked by the "Get Location Alternative" button.
     *
     * 
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void getIntentMap(View v) {

        // If Google Play Services is available
        if (servicesConnected()) {        	            
        	
        	String latlng = mLatLng.getText().toString();
        	String[] latlng_bloques = latlng.split(",");
        			
        	double latitude = Double.parseDouble(latlng_bloques[0]);
        	double longitude = Double.parseDouble(latlng_bloques[1]);        	
        	String label = "Location";
        	String uriBegin = "geo:" + latitude + "," + longitude;
        	String query = latitude + "," + longitude + "(" + label + ")";
        	String encodedQuery = Uri.encode(query);
        	String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        	Uri uri = Uri.parse(uriString);
        	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        	startActivity(intent);
        	
            
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  // Inflate the menu; this adds items to the action bar if it is present.
		   getMenuInflater().inflate(R.menu.main, menu);		
	
			// Locate MenuItem with ShareActionProvider
		    MenuItem item = menu.findItem(R.id.menu_item_share);
 
            // Fetch and store ShareActionProvider
		    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		    
		return true;
	}
       
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	System.out.println("onOptionsItemSelected()");
    	
    	switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, Preferences.class);
			startActivity(intent);
			return true;		

		default:
			// TODO Auto-generated method stub
			return super.onOptionsItemSelected(item);
		}
    	
		
	}
    
    public void setShareIntent(String addrees) {
        if (mShareActionProvider != null) {
        	 Intent shareIntent = ShareCompat.IntentBuilder.from(this)
 		            .setType("text/plain").setText(addrees).getIntent();
 		     
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    
    private void startXML() {   	
    	      	
    	   try {    	
    	      fout = openFileOutput("location.xml", MODE_PRIVATE);    	
    	   } catch (FileNotFoundException e) {    	
    	      Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    	   }

    	   serializer = Xml.newSerializer();
    	
    	   try {
    	
    	      serializer.setOutput(fout, "UTF-8");    	
    	      serializer.startDocument(null, true);    	
    	      serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
   	
    	   } catch (Exception e) {    
    	      Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    	   }
    	
    	}
    private void updatesXML() {
    	
    	if(!startXml){
    		startXml = true;
    		startXML();
    	}
    	// Get the current location
        Location currentLocation = mLocationClient.getLastLocation();
        
        String locationSave = LocationUtils.getLatLng(this, currentLocation);
        try {
        	serializer.startTag(null, "location");       	
    	
        	serializer.text(locationSave);    
	      
        	serializer.endTag(null, "location"); 
        } catch (Exception e) {    
        	e.printStackTrace();
        }	
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

    public void saveXml(View v) {
    	saveXML = true;
    }
    
    public void notSaveXml(View v) {
    	saveXML = false;
    }

    public void openContact(View v) {
    	contacts.selectContact();
    }
	/*
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
	        if (connectionResult.hasResolution()) {
	            try {

	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(
	                        (Activity)this.context,
	                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

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
	 */
}

