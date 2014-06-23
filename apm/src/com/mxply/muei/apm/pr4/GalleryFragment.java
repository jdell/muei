package com.mxply.muei.apm.pr4;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mxply.muei.apm.R;
import com.mxply.muei.apm.pr3.LocationResult;
import com.mxply.muei.apm.pr3.LocationResultArrayAdapter;
import com.mxply.muei.apm.pr3.Dash3Activity.Geolocation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class GalleryFragment extends Fragment { 
/*

	GalleryAdapter adapter =null; 
	ArrayList<String> files = new ArrayList<String>();
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View rootView = inflater.inflate(R.layout.pr4_fragment_gallery, container, false);

	        GridView gridView = (GridView)rootView.findViewById(R.id.gallery);
	        gridView.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
	            {
	            	Toast.makeText(getActivity(), "test: " + files.get(position), Toast.LENGTH_SHORT).show();
	                //Intent i = new Intent(mActivity, ActvityToCall.class);
	                //mActivity.startActivity(i);
	            }
			});
			adapter = new GalleryAdapter(this.getActivity(), files);
			
			gridView.setAdapter(adapter);

	        return rootView;
	    }
	    
	    @Override
	    public void setMenuVisibility(final boolean visible) {
	        super.setMenuVisibility(visible);
	        if (visible) {
				(new LoadImagesTask()).execute(((Dash4Activity)this.getActivity()).getAlbumDir());
	    	}
	     }
	    
	    
	   protected class LoadImagesTask extends AsyncTask<File, Void, Void>{

	        @Override
	        protected void onPreExecute() {
	        	// TODO Auto-generated method stub
	        	super.onPreExecute();
	        	files.clear();
	        }
		@Override
		protected Void doInBackground(File... params) {
			if (params!=null)
				files.addAll(Arrays.asList(params[0].list()));
				
			
            return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			adapter.notifyDataSetChanged();
		}
	   }
	   */
}
