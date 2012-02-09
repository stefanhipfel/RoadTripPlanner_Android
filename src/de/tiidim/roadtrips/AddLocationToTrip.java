package de.tiidim.roadtrips;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Provides a form with which a given location can be added to a trip
 * 
 * @author stefanhipfel, patrickpermien
 * @see Location
 * @see Trip
 */
public class AddLocationToTrip extends Activity {
	private long chosenLocation_id; // caches the id of the chosen location.
	private long trip_id; // caches the trip id from the intent extras.
	private EditText locRoutingEditText; // editable textinput GUI element
	private LocationSelectionHelper locationSelectionHelper; // helper class
																// instance

	/**
	 * adds new location to existing trip; persists given input data
	 * 
	 * @param v
	 */
	public void onAddLocationButtonClicked(View v) {
		// TODO add validation (for example, arrival must not be before trip
		// start)
		try {
			Address a = locationSelectionHelper.chosenLocation;
			long loc_id = de.tiidim.roadtrips.Location.createOrFetchLocation(
					a.getLatitude(), a.getLongitude(), a.getLocality());

			chosenLocation_id = loc_id;
			locRoutingEditText = locationSelectionHelper.getEditText();
		} catch (NullPointerException e) {
			Toast.makeText(getApplicationContext(),
					R.string.error_loc,
					Toast.LENGTH_LONG).show();
			return;
		}
		CustomDateFormat sdf = new CustomDateFormat();

		final DatePicker startPicker = (DatePicker) findViewById(R.id.addloc_editArrivalTime);
		Date arrivalDate = new Date(startPicker.getYear() - 1900,
				startPicker.getMonth(), startPicker.getDayOfMonth());
		String arrivalTime = sdf.format(arrivalDate);

		final CheckBox stayOvernightCbx = (CheckBox) findViewById(R.id.addloc_stayOvernight);
		boolean stayOvernight = stayOvernightCbx.isChecked();

		TripRouting.createRouting(chosenLocation_id, trip_id, arrivalTime,
				stayOvernight);

		Intent myIntent = new Intent(v.getContext(), TripDetailView.class);

		myIntent.putExtra(TripsDbAdapter.KEY_ROWID, trip_id);
		startActivityForResult(myIntent, 0);
	}

	/**
	 * Called when the activity is first created.
	 **/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addlocationtotrip);
		TripList.mDbHelper.open();

		// retrieve passed-on param
		Bundle extras = getIntent().getExtras();

		locationSelectionHelper = new LocationSelectionHelper(this);

		// define desired trip

		try {
			trip_id = extras.getLong(TripsDbAdapter.KEY_ROWID);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "invalid id",
					Toast.LENGTH_LONG).show();
			return;
		}

		setupListeners();
	}

	/**
	 * redirects back to home.
	 * 
	 * @param v
	 *            the sender view
	 */
	public void onCancelButtonClicked(View v) {
		Intent myIntent = new Intent(v.getContext(), TripList.class);
		startActivityForResult(myIntent, 0);
	}

	@Override
	protected void onDestroy() {
		TripsDbAdapter.mDb.close();
		super.onDestroy();
	}

	/**
	 * helper method that instantiates a new listener
	 * 
	 * @return new customized OnFocusChangeListener instance
	 */
	private OnFocusChangeListener getOnFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				locationSelectionHelper.reactOnFocusChange(v, hasFocus,
						locationSelectionHelper.getEditText(), v.getContext());


			}
		};
	}

	/**
	 * helper method that registers our listeners
	 */
	private void setupListeners() {
		locRoutingEditText = (EditText) findViewById(R.id.addloc_locName);
		if (null == locationSelectionHelper) {
			locationSelectionHelper = new LocationSelectionHelper(
					getApplicationContext());
		}
		locationSelectionHelper.setEditText(locRoutingEditText);
		locRoutingEditText.setOnFocusChangeListener(getOnFocusChangeListener());
	}
}