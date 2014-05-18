package com.mxply.muei.apm.pr4;
import java.io.File;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;

import com.mxply.muei.apm.AboutActivity;
import com.mxply.muei.apm.R;
import com.mxply.muei.apm.pr2.Dash2Activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("NewApi")
public class Dash4Activity extends FragmentActivity implements ActionBar.TabListener {
	
		private ViewPager viewPager;
	    private TabsPagerAdapter mAdapter;
	    private ActionBar actionBar;
	    // Tab titles
	    private String[] tabs = { "Cámara", "Galeria" };

		private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
		
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_dash4);
	 
	        viewPager = (ViewPager) findViewById(R.id.pager);
	        actionBar = getActionBar();
	        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
	 
	        viewPager.setAdapter(mAdapter);
	        actionBar.setHomeButtonEnabled(false);
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
	 
	        // Adding Tabs
	        for (String tab_name : tabs) {
	            actionBar.addTab(actionBar.newTab().setText(tab_name)
	                    .setTabListener(this));
	        }
	        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	        	 
	            @Override
	            public void onPageSelected(int position) {
	                // on changing the page
	                // make respected tab selected
	                actionBar.setSelectedNavigationItem(position);
	            }
	         
	            @Override
	            public void onPageScrolled(int arg0, float arg1, int arg2) {
	            }
	         
	            @Override
	            public void onPageScrollStateChanged(int arg0) {
	            }
	        });
	        

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
			} else {
				mAlbumStorageDirFactory = new BaseAlbumDirFactory();
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
					Intent intent = new Intent(Dash4Activity.this, AboutActivity.class);
					startActivity(intent);
					break;
				default:
					res =super.onOptionsItemSelected(item);
			}
		   return res;
		   
		}
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	       viewPager.setCurrentItem(tab.getPosition());
   	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public class TabsPagerAdapter extends FragmentPagerAdapter {
		 
	    public TabsPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
	 
	    @Override
	    public Fragment getItem(int index) {
	 
	        switch (index) {
	        case 0:
	            // Top Rated fragment activity
	            return new CameraFragment();
	        case 1:
	            // Games fragment activity
	            return new GalleryFragment();
	        }
	 
	        return null;
	    }
	 
	    @Override
	    public int getCount() {
	        // get item count - equal to number of tabs
	        return 2;
	    }
	 
	}

	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	public File getAlbumDir() {
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
}
