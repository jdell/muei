package es.udc.fic.muei.apm.geolocalizacion.fragments;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import es.udc.fic.muei.apm.geolocalizacion.LocationMainActivity;
import es.udc.fic.muei.apm.geolocalizacion.R;
import es.udc.fic.muei.apm.geolocalizacion.common.LocationUtils;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class DirectLocationFragment extends Fragment {
    // Handles to UI widgets
    private TextView txtLatLng;
    private EditText txtAddress;
    private Button btnGetLatLng;
    private Button btnViewMap;
    
    private Activity activity;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	 View rootView = inflater.inflate(R.layout.fragment_direct_location, container, false);
         
    	 activity = this.getActivity();
    	 
    	 txtLatLng = (TextView)rootView.findViewById(R.id.txtLatLng);
    	 txtAddress = (EditText)rootView.findViewById(R.id.txtAddress);
    	 btnGetLatLng = (Button)rootView.findViewById(R.id.btnGetLatLng);
    	 btnGetLatLng.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	String address = txtAddress.getText().toString();      
		        if (address!=null && address.length()>0){
			        (new GetLocationAlternativeTask(activity)).execute(address);
		        }else{
			          Toast.makeText(activity, "No address specified", Toast.LENGTH_SHORT).show();
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
    	     
    	 
    	 return rootView;
    }
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
	        	jsonObject = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
	        	float lon = (float) jsonObject.getDouble("lng");
	        	float lat = (float) jsonObject.getDouble("lat");
	        	
	            // Display the current location in the UI
	           return ""+ lat + ", " +lon;	
             

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
            txtLatLng.setText(latLng);
        }
    }
    
    
}
