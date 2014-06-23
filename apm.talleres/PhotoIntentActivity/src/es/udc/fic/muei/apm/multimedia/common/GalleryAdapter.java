package es.udc.fic.muei.apm.multimedia.common;

import java.io.File;
import java.util.ArrayList;

import com.google.android.gms.drive.internal.r;

import es.udc.fic.muei.apm.multimedia.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
 
public class GalleryAdapter extends ArrayAdapter<GalleryItem> {
		
	static class ItemViewHolder{
		TextView txtName;
		TextView txtDate;
		ImageView imgPicture;
		TextView txtMap;
	}
	
    private Activity mContext;
    private ArrayList<GalleryItem> items;
    
    public static final String PREFS_NAME = "MyPrefsFile";
	
	SharedPreferences mPrefs;
    

    public GalleryAdapter(Context c,int resource,ArrayList<GalleryItem> objects) {
        super(c,resource,objects);
        mContext = (Activity) c;
        items = objects;
    }
  
    public View getView(int position, View convertView, ViewGroup parent) {
    	ItemViewHolder viewHolder;
    	if(convertView==null){
    		LayoutInflater inflater = mContext.getLayoutInflater();

    		viewHolder = new ItemViewHolder();
    		/*TODO> aqui viewHolder
    		convertView = inflater.inflate(R.layout.element_gallery, null);
    		viewHolder.imgPicture = (ImageView) convertView.findViewById(R.id.imageView3);
    		viewHolder.txtName = (TextView) convertView.findViewById(R.id.textView3);
    		viewHolder.txtDate = (TextView) convertView.findViewById(R.id.textView4);
    		viewHolder.txtMap = (TextView) convertView.findViewById(R.id.textView5);
    		convertView.setTag(viewHolder);
    		*/
    	}else{
    		viewHolder = (ItemViewHolder) convertView.getTag();
    	}
    	
    	GalleryItem item = items.get(position); 
    	if(item!=null){
    		viewHolder.imgPicture.setImageBitmap(item.getImage());
    		viewHolder.txtName.setText(item.getName());
    		viewHolder.txtDate.setText(item.getDate());
    		viewHolder.txtMap.setText("Maps");
    		viewHolder.txtMap.setOnClickListener(new CustomOnClickListener(item.getName()) {  
    	        public void onClick(View v)
                {
    	        	String an = mContext.getString(R.string.album_name);
    	        	File storageDir=null;
    	        	AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    	        	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
    	    			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
    	    		} else {
    	    			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
    	    		}
    	        	if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
    	    			
    	    			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(an);
    	        	}	
    	        	
    	        	 mPrefs = mContext.getSharedPreferences(PREFS_NAME,0);
    	        	 ;
    	        	String location = mPrefs.getString(storageDir + "/" + this.getFilename(),"No location");    	        	
    	        	
    	        	String[] latlng_bloques = location.split(" - ");
    	        	latlng_bloques[0] = latlng_bloques[0].replace(",", ".")	;	
    	        	latlng_bloques[1] = latlng_bloques[1].replace(",", ".")	;
    	        	double latitude = Double.parseDouble(latlng_bloques[0]);
    	        	double longitude = Double.parseDouble(latlng_bloques[1]);        	
    	        	String label = "Location";
    	        	String uriBegin = "geo:" + latitude + "," + longitude;
    	        	String query = latitude + "," + longitude + "(" + label + ")";
    	        	String encodedQuery = Uri.encode(query);
    	        	String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
    	        	Uri uri = Uri.parse(uriString);
    	        	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
    	        	v.getContext().startActivity(intent);
                }
             });
    	}	
    	return convertView;
    	
    }

    public class CustomOnClickListener implements OnClickListener
    {
    	private String filename; 	
    	public CustomOnClickListener(String filename) {
    		this.filename = filename;
		}
    	
    	@Override
    	public void onClick(View v) {
    		
    	}

		public String getFilename() {
			return filename;
		}
		
		public void setFilename(String filename) {
			this.filename = filename;
		}
    }
 
    
}
