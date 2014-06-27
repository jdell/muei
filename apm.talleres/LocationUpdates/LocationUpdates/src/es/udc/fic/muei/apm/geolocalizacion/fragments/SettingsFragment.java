
 package es.udc.fic.muei.apm.geolocalizacion.fragments;

import es.udc.fic.muei.apm.geolocalizacion.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}
	
}

