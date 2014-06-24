package es.udc.fic.muei.apm.multimedia.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import es.udc.fic.muei.apm.multimedia.R;
import es.udc.fic.muei.apm.multimedia.common.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class CameraFragment extends Fragment implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {


	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	
	private ImageView mImageView;
	private View rootView;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private String mCurrentPhotoPath;
	private Bitmap mImageBitmap;

	
	// Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    private String latLng = "";
    
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences mPrefs;    
    SharedPreferences.Editor mEditor;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        
        
        mPrefs = this.getActivity().getSharedPreferences(PREFS_NAME,0);

        mEditor = mPrefs.edit();      
        
        mImageView = (ImageView) rootView.findViewById(R.id.imageView1);
        
        Button picBtn = (Button) rootView.findViewById(R.id.btnIntend);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
		if(savedInstanceState!=null){
			mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
			mImageView.setImageBitmap(mImageBitmap);
			mImageView.setVisibility(
					savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
							ImageView.VISIBLE : ImageView.INVISIBLE
			);
		}
        
		 /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        
		
		mLocationClient = new LocationClient(getActivity(), this, this);
		
        return rootView;
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == android.app.Activity.RESULT_OK ) {
				handleBigCameraPhoto();
			}
			break;
		} // ACTION_TAKE_PHOTO_B

		} // switch
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
				
		super.onSaveInstanceState(outState);
	}
    
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if(savedInstanceState!=null){
			mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
			mImageView.setImageBitmap(mImageBitmap);
			mImageView.setVisibility(
					savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
							ImageView.VISIBLE : ImageView.INVISIBLE
			);
		}

	}
		
    @Override
    public void onStart() {

        super.onStart();

        if(!mLocationClient.isConnected()){
        	  mLocationClient.connect();
        }
      
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	//********** Images *************************
	Button.OnClickListener mTakePicOnClickListener = 
			new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
			}
	};
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(rootView.getContext(), intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		mImageBitmap = bitmap;
		
		
		
		getLocation();
		mEditor.putString(mCurrentPhotoPath, latLng);
        mEditor.commit();
        
        /* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);
		
	}
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    rootView.getContext().sendBroadcast(mediaScanIntent);
	}
	
	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}
	
	//********** Location ***********************
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(rootView.getContext());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            
            return false;
        }
    }
    public void getLocation() {    
		// If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

            // Display the current location in the UI
            latLng = LocationUtils.getLatLng(rootView.getContext(), currentLocation);             
            
            mLocationClient.disconnect();
        }
    }
    
    /*********/
 // Method:
    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
    	List<Intent> targetedShareIntents = new ArrayList<Intent>();
    	List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
    	Intent chooserIntent;
     
    	Intent dummy = new Intent(prototype.getAction());
    	dummy.setType(prototype.getType());
    	List<ResolveInfo> resInfo = this.getActivity().getPackageManager().queryIntentActivities(dummy, 0);

		CharSequence title  = null;
    	if (!resInfo.isEmpty()) {
    		for (ResolveInfo resolveInfo : resInfo) {
    			if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
    				continue;
     
    			HashMap<String, String> info = new HashMap<String, String>();
    			info.put("packageName", resolveInfo.activityInfo.packageName);
    			info.put("className", resolveInfo.activityInfo.name);
    			info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(this.getActivity().getPackageManager())));
    			intentMetaInfo.add(info);
    		}
     
    		if (!intentMetaInfo.isEmpty()) {
    			// sorting for nice readability
    			Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
    				@Override
    				public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
    					return map.get("simpleName").compareTo(map2.get("simpleName"));
    				}
    			});
     
    			// create the custom intent list
    			for (HashMap<String, String> metaInfo : intentMetaInfo) {
    				Intent targetedShareIntent = (Intent) prototype.clone();
    				targetedShareIntent.setPackage(metaInfo.get("packageName"));
    				targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
    				targetedShareIntents.add(targetedShareIntent);
    			}
     
    			chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), title);
    			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
    			return chooserIntent;
    		}
    	}
     
    	return Intent.createChooser(prototype, title);
    }
}
