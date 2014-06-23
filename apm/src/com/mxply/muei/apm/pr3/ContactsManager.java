package com.mxply.muei.apm.pr3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

public class ContactsManager {

	 private final int CONTACT_PICKER_RESULT = 898;
	 
	 private Activity activity;
	 public ContactsManager(Activity activity){
	  this.activity = activity;  
	 }
	 
	 public interface onSelectedEmail{ void onSuccess(String email); void onFailure();};

	 
	 public void selectContact(){
	  Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);  
	  this.activity.startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT); 
	 }
	 

	 public void onActivityResult(int requestCode, int resultCode, Intent data, onSelectedEmail onSelectedEmailHandler){
	  if(requestCode == CONTACT_PICKER_RESULT){
		  if (resultCode == Activity.RESULT_OK) {
			  Cursor cursor = null;
			  String address = "";
			  try {
				  Uri result = data.getData();
				  String id = result.getLastPathSegment();
				  cursor = this.activity.getContentResolver().query(StructuredPostal.CONTENT_URI, null, StructuredPostal.CONTACT_ID + "=?", new String[] { id }, null);
				  int index = cursor.getColumnIndex(StructuredPostal.DATA);
				  if (cursor.moveToFirst()){
					  address = cursor.getString(index);  
				  }
			  } catch (Exception e) {
				  
			  } finally {
				  if (cursor != null){
                      cursor.close();  
				  }  
				  if (address.length() == 0){
	                    onSelectedEmailHandler.onFailure();  
				  }
				  else{
	                    onSelectedEmailHandler.onSuccess(address);  
				  }
			  }        
	       } 
		  else {
			  onSelectedEmailHandler.onFailure();
	      }
	  }
	 }
}
