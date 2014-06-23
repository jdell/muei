package es.udc.fic.muei.apm.multimedia;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}

//
//public class PhotoIntentActivity extends Activity {
//
//	private static final int ACTION_TAKE_PHOTO_B = 1;
//	private static final int ACTION_TAKE_PHOTO_S = 2;
//	private static final int ACTION_TAKE_VIDEO = 3;
//
//	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
//	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
//	private ImageView mImageView;
//	private Bitmap mImageBitmap;
//
//	private static final String VIDEO_STORAGE_KEY = "viewvideo";
//	private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
//	private VideoView mVideoView;
//	private Uri mVideoUri;
//
//	private String mCurrentPhotoPath;
//
//	private static final String JPEG_FILE_PREFIX = "IMG_";
//	private static final String JPEG_FILE_SUFFIX = ".jpg";
//
//	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
//
//	private String getAlbumName() {
//		return getString(R.string.album_name);
//	}
//
//	
//	private File getAlbumDir() {
//		File storageDir = null;
//
//		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//			
//			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
//
//			if (storageDir != null) {
//				if (! storageDir.mkdirs()) {
//					if (! storageDir.exists()){
//						Log.d("CameraSample", "failed to create directory");
//						return null;
//					}
//				}
//			}
//			
//		} else {
//			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
//		}
//		
//		return storageDir;
//	}
//
//	private File createImageFile() throws IOException {
//		// Create an image file name
//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
//		File albumF = getAlbumDir();
//		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
//		return imageF;
//	}
//
//	private File setUpPhotoFile() throws IOException {
//		
//		File f = createImageFile();
//		mCurrentPhotoPath = f.getAbsolutePath();
//		
//		return f;
//	}
//
//	private void setPic() {
//
//		/* There isn't enough memory to open up more than a couple camera photos */
//		/* So pre-scale the target bitmap into which the file is decoded */
//
//		/* Get the size of the ImageView */
//		int targetW = mImageView.getWidth();
//		int targetH = mImageView.getHeight();
//
//		/* Get the size of the image */
//		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//		bmOptions.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//		int photoW = bmOptions.outWidth;
//		int photoH = bmOptions.outHeight;
//		
//		/* Figure out which way needs to be reduced less */
//		int scaleFactor = 1;
//		if ((targetW > 0) || (targetH > 0)) {
//			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
//		}
//
//		/* Set bitmap options to scale the image decode target */
//		bmOptions.inJustDecodeBounds = false;
//		bmOptions.inSampleSize = scaleFactor;
//		bmOptions.inPurgeable = true;
//
//		/* Decode the JPEG file into a Bitmap */
//		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//		
//		/* Associate the Bitmap to the ImageView */
//		mImageView.setImageBitmap(bitmap);
//		mVideoUri = null;
//		mImageView.setVisibility(View.VISIBLE);
//		mVideoView.setVisibility(View.INVISIBLE);
//	}
//
//	private void galleryAddPic() {
//		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
//			File f = new File(mCurrentPhotoPath);
//		    Uri contentUri = Uri.fromFile(f);
//		    mediaScanIntent.setData(contentUri);
//		    this.sendBroadcast(mediaScanIntent);
//	}
//
//	private void dispatchTakePictureIntent(int actionCode) {
//
//		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//		switch(actionCode) {
//		case ACTION_TAKE_PHOTO_B:
//			File f = null;
//			
//			try {
//				f = setUpPhotoFile();
//				mCurrentPhotoPath = f.getAbsolutePath();
//				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//			} catch (IOException e) {
//				e.printStackTrace();
//				f = null;
//				mCurrentPhotoPath = null;
//			}
//			break;
//
//		default:
//			break;			
//		} // switch
//
//		startActivityForResult(takePictureIntent, actionCode);
//	}
//
//	private void dispatchTakeVideoIntent() {
//		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
//	}
//
//	private void handleSmallCameraPhoto(Intent intent) {
//		Bundle extras = intent.getExtras();
//		mImageBitmap = (Bitmap) extras.get("data");
//		mImageView.setImageBitmap(mImageBitmap);
//		mVideoUri = null;
//		mImageView.setVisibility(View.VISIBLE);
//		mVideoView.setVisibility(View.INVISIBLE);
//	}
//
//	private void handleBigCameraPhoto() {
//
//		if (mCurrentPhotoPath != null) {
//			setPic();
//			galleryAddPic();
//			mCurrentPhotoPath = null;
//		}
//
//	}
//
//	private void handleCameraVideo(Intent intent) {
//		mVideoUri = intent.getData();
//		mVideoView.setVideoURI(mVideoUri);
//		mImageBitmap = null;
//		mVideoView.setVisibility(View.VISIBLE);
//		mImageView.setVisibility(View.INVISIBLE);
//	}
//
//	Button.OnClickListener mTakePicOnClickListener = 
//		new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
//		}
//	};
//
//	Button.OnClickListener mTakePicSOnClickListener = 
//		new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
//		}
//	};
//
//	Button.OnClickListener mTakeVidOnClickListener = 
//		new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			dispatchTakeVideoIntent();
//		}
//	};
//
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//
//		mImageView = (ImageView) findViewById(R.id.imageView1);
//		mVideoView = (VideoView) findViewById(R.id.videoView1);
//		mImageBitmap = null;
//		mVideoUri = null;
//
//		Button picBtn = (Button) findViewById(R.id.btnIntend);
//		setBtnListenerOrDisable( 
//				picBtn, 
//				mTakePicOnClickListener,
//				MediaStore.ACTION_IMAGE_CAPTURE
//		);
//
//		Button picSBtn = (Button) findViewById(R.id.btnIntendS);
//		setBtnListenerOrDisable( 
//				picSBtn, 
//				mTakePicSOnClickListener,
//				MediaStore.ACTION_IMAGE_CAPTURE
//		);
//
//		Button vidBtn = (Button) findViewById(R.id.btnIntendV);
//		setBtnListenerOrDisable( 
//				vidBtn, 
//				mTakeVidOnClickListener,
//				MediaStore.ACTION_VIDEO_CAPTURE
//		);
//		
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
//			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
//		} else {
//			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case ACTION_TAKE_PHOTO_B: {
//			if (resultCode == RESULT_OK) {
//				handleBigCameraPhoto();
//			}
//			break;
//		} // ACTION_TAKE_PHOTO_B
//
//		case ACTION_TAKE_PHOTO_S: {
//			if (resultCode == RESULT_OK) {
//				handleSmallCameraPhoto(data);
//			}
//			break;
//		} // ACTION_TAKE_PHOTO_S
//
//		case ACTION_TAKE_VIDEO: {
//			if (resultCode == RESULT_OK) {
//				handleCameraVideo(data);
//			}
//			break;
//		} // ACTION_TAKE_VIDEO
//		} // switch
//	}
//
//	// Some lifecycle callbacks so that the image can survive orientation change
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
//		outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
//		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
//		outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
//		super.onSaveInstanceState(outState);
//	}
//
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
//		mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
//		mImageView.setImageBitmap(mImageBitmap);
//		mImageView.setVisibility(
//				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
//						ImageView.VISIBLE : ImageView.INVISIBLE
//		);
//		mVideoView.setVideoURI(mVideoUri);
//		mVideoView.setVisibility(
//				savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ? 
//						ImageView.VISIBLE : ImageView.INVISIBLE
//		);
//	}
//
//	/**
//	 * Indicates whether the specified action can be used as an intent. This
//	 * method queries the package manager for installed packages that can
//	 * respond to an intent with the specified action. If no suitable package is
//	 * found, this method returns false.
//	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
//	 *
//	 * @param context The application's environment.
//	 * @param action The Intent action to check for availability.
//	 *
//	 * @return True if an Intent with the specified action can be sent and
//	 *         responded to, false otherwise.
//	 */
//	public static boolean isIntentAvailable(Context context, String action) {
//		final PackageManager packageManager = context.getPackageManager();
//		final Intent intent = new Intent(action);
//		List<ResolveInfo> list =
//			packageManager.queryIntentActivities(intent,
//					PackageManager.MATCH_DEFAULT_ONLY);
//		return list.size() > 0;
//	}
//
//	private void setBtnListenerOrDisable( 
//			Button btn, 
//			Button.OnClickListener onClickListener,
//			String intentName
//	) {
//		if (isIntentAvailable(this, intentName)) {
//			btn.setOnClickListener(onClickListener);        	
//		} else {
//			btn.setText( 
//				getText(R.string.cannot).toString() + " " + btn.getText());
//			btn.setClickable(false);
//		}
//	}
//
//}
