package com.mxply.muei.apm.pr1;

import com.mxply.muei.apm.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Dash1SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
	}
}
