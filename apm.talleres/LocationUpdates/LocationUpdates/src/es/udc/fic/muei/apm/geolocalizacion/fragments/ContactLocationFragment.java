package es.udc.fic.muei.apm.geolocalizacion.fragments;

import es.udc.fic.muei.apm.geolocalizacion.LocationMainActivity;
import es.udc.fic.muei.apm.geolocalizacion.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ContactLocationFragment extends Fragment{
	 private final int CONTACT_PICKER_RESULT = 898;
	 private Activity activity;
	 private TextView txtAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
    	this.activity = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_contact_location, container, false);
        txtAddress = (TextView)rootView.findViewById(R.id.txtAddress);
        Button btnOpenContact = (Button) rootView.findViewById(R.id.btnOpenContact);
        btnOpenContact.setOnClickListener(new View.OnClickListener()
		{
		    public void onClick(View v)
		    {		 
		    	activity.startActivityForResult(new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI), CONTACT_PICKER_RESULT); 
		    }
		});
        
        return rootView;
    }
	 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(requestCode == CONTACT_PICKER_RESULT){
    		if (resultCode == Activity.RESULT_OK) {
    			Cursor cursor = null;
    			String address = "";
    			try {
					  Uri result = data.getData();
					  String id = result.getLastPathSegment();
					  cursor = this.activity.getContentResolver().query(StructuredPostal.CONTENT_URI, null, StructuredPostal.CONTACT_ID + "=?", new String[] { id }, null);
					  int idx = cursor.getColumnIndex(StructuredPostal.DATA);
					  if (cursor.moveToFirst()) {
						  address = cursor.getString(idx);
					  }
				  } catch (Exception e) {
					  
				  } finally {
					  if (cursor != null) cursor.close();
					  if (address.length() != 0){
						  //Success
			            
			             txtAddress.setText(address);
			             
			             ((LocationMainActivity)this.activity).setShareIntent(address);
					  }
					  else{
						  //Failure
				          Toast.makeText(activity, "Failure trying to get contact address", Toast.LENGTH_SHORT).show();
					  }
				  }
			  } else {
				  //Failure       
				  Toast.makeText(activity, "Failure trying to get contact address", Toast.LENGTH_SHORT).show();					
			  }
		  }
    }
}
