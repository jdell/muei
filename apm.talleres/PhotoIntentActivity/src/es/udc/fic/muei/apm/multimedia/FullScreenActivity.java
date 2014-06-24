package es.udc.fic.muei.apm.multimedia;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import es.udc.fic.muei.apm.multimedia.common.AlbumStorageDirFactory;
import es.udc.fic.muei.apm.multimedia.common.BaseAlbumDirFactory;
import es.udc.fic.muei.apm.multimedia.common.FroyoAlbumDirFactory;
import es.udc.fic.muei.apm.multimedia.R;

public class FullScreenActivity extends Activity {
	
public static final String PREFS_NAME = "MyPrefsFile";
	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	   SharedPreferences mPrefs;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen);
		
		 mPrefs = getSharedPreferences(PREFS_NAME,0);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
    	
		Intent intent = getIntent();
		String fileName = intent.getStringExtra("FILE_NAME");
				
		ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
		
		int targetW = imageView2.getWidth();
		int targetH = imageView2.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(getAlbumDir() + "/" + fileName, bmOptions);
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
		Bitmap bitmap = BitmapFactory.decodeFile(getAlbumDir() + "/" + fileName, bmOptions);
			
		/* Associate the Bitmap to the ImageView */
		imageView2.setImageBitmap(bitmap);
		imageView2.setVisibility(View.VISIBLE);
		
		String fileNameKey = getAlbumDir() + "/" + fileName; 
		String location = mPrefs.getString(fileNameKey,"No location");
		TextView textView = (TextView) findViewById(R.id.textViewLoc);
		textView.setText(" Location: " +location);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_screen, menu);
		return true;
	}

}
