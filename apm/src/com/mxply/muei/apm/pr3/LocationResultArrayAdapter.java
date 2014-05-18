package com.mxply.muei.apm.pr3;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mxply.muei.apm.R;
import com.mxply.muei.apm.pr3.LocationResult;

public class LocationResultArrayAdapter extends ArrayAdapter<LocationResult> {
	  private final Context context;
	  private final ArrayList<LocationResult> values;
      private static LayoutInflater inflater = null;

	  public LocationResultArrayAdapter(Context context, ArrayList<LocationResult> values) {
	    super(context, R.layout.pr3_fragment_list_row, values);
	    this.context = context;
	    this.values = values;

	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolder;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.pr3_fragment_list_row, parent, false);

		    viewHolder = new ViewHolderItem();
		    viewHolder.txtResultAddress = (TextView) convertView.findViewById(R.id.rowResultAddress);
		    viewHolder.txtResultMethod = (TextView) convertView.findViewById(R.id.rowResultMethod);
		    viewHolder.txtResultLatitude = (TextView) convertView.findViewById(R.id.rowResultLatitude);
		    viewHolder.txtResultLongitude = (TextView) convertView.findViewById(R.id.rowResultLongitude);
		
		    convertView.setTag(viewHolder);
		
		}else{
		    viewHolder = (ViewHolderItem) convertView.getTag();
		}
			    
	    LocationResult lr = values.get(position);

	    viewHolder.txtResultAddress.setText(lr.getAddress());
	    viewHolder.txtResultMethod.setText(lr.getMethod());
	    viewHolder.txtResultLatitude.setText(String.valueOf(lr.getLocation().getLatitude()));
	    viewHolder.txtResultLongitude.setText(String.valueOf(lr.getLocation().getLongitude()));

	    return convertView;
	  }
		static class ViewHolderItem {
		    TextView txtResultAddress;
		    TextView txtResultMethod;
		    TextView txtResultLatitude;
		    TextView txtResultLongitude;
		}
	} 
