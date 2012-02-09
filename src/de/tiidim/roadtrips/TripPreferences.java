package de.tiidim.roadtrips;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity for viewing and editing preferences related to our app
 * @author stefanhipfel
 *
 */
public class TripPreferences extends PreferenceActivity {

	/** Called when the activity is first created. */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

	}

}