package es.udc.fic.muei.apm.geolocalizacion;

import es.udc.fic.muei.apm.geolocalizacion.fragments.SettingsFragment;
import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}
}
