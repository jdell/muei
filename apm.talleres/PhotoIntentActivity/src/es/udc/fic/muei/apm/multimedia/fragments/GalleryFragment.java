package es.udc.fic.muei.apm.multimedia.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.udc.fic.muei.apm.multimedia.FullScreenActivity;
import es.udc.fic.muei.apm.multimedia.R;
import es.udc.fic.muei.apm.multimedia.common.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

public class GalleryFragment extends Fragment {
	private View rootView;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;	
	ArrayList<GalleryItem> itemList = new ArrayList<GalleryItem>();
	GridView gridview;
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
    	rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
    	
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
    	
    	gridview = (GridView) rootView.findViewById(R.id.gridview);
    	         
        return rootView;
    }
    
    
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
        	(new GalleryFragment.LoadImagesTask(rootView.getContext())).execute();
    	}
     }
    public void refreshGallery(){
    	gridview.setAdapter(new GalleryAdapter(rootView.getContext(), R.layout.galleryitem, itemList));
    	gridview.setOnItemClickListener(new OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                   
           	Intent intent = new Intent(rootView.getContext(), FullScreenActivity.class);
            	intent.putExtra("FILE_NAME",itemList.get(position).getName());
        		startActivity(intent);
 
    		}
    	});
   
    }

    
    
    protected class LoadImagesTask extends AsyncTask<Void, Void, Void> {

        Context localContext;

        public LoadImagesTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        @Override
		protected Void doInBackground(Void... arg0) {
           
        	try {
        		itemList.clear();        
            	File[] files = getAlbumDir().listFiles();
            	Integer[] mThumbIdsIntegers;
            	for (File f:files){
            		Date lastModDate = new Date(f.lastModified());
            		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            		
            		GalleryItem eg = new GalleryItem(convertToBitmap(f), readFileName(f),formatter.format(lastModDate));
            		itemList.add(eg);     	
            	}   	            	         	
                 
                 return null;
             // Catch network or other I/O problems.
            } catch (Exception exception) {
            	// print the stack trace
            	exception.printStackTrace();
            	return null;               
             }                 
        }

		@Override
		protected void onPostExecute(Void result) {
			refreshGallery();
		}

    }	
    
	//************************
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
	
	public static Bitmap convertToBitmap(File file) 
	{
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = 150;
		int targetH = 150;
		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
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
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
		
		return bitmap;
	}

	public String readFileName(File file){
	    return file.getName();
	}
}
