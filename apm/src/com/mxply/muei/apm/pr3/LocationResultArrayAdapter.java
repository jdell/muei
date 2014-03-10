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

	  public LocationResultArrayAdapter(Context context, ArrayList<LocationResult> values) {
	    super(context, R.layout.pr3_fragment_list_row, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.pr3_fragment_list_row, parent, false);
	    TextView txtResultMethod = (TextView) rowView.findViewById(R.id.rowResultMethod);
	    TextView txtResultLatitude = (TextView) rowView.findViewById(R.id.rowResultLatitude);
	    TextView txtResultLongitude = (TextView) rowView.findViewById(R.id.rowResultLongitude);
	    
	    LocationResult lr = values.get(position);

	    txtResultMethod.setText(lr.getMethod());
	    txtResultLatitude.setText(String.valueOf(lr.getLocation().getLatitude()));
	    txtResultLongitude.setText(String.valueOf(lr.getLocation().getLongitude()));

	    return rowView;
	  }
	} 
