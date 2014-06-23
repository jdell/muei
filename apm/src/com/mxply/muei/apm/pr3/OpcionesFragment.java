package com.mxply.muei.apm.pr3;

import com.mxply.muei.apm.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class OpcionesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}
}
