package de.tiidim.roadtrips;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Most unsurprisingly serves as form to define and persist a new trip.
 * 
 * @author patrickpermien, stefanhipfel
 */
public class TripAdd extends Activity {

	private static final int ACTIVITY_LIST = 0;

	private EditText mTitleText;
	private EditText locStartEditText;
	private EditText locEndEditText;

	private long chosenStartLocation_id;
	private long chosenEndLocation_id;

	private LocationSelectionHelper selectionHelperStart;
	private LocationSelectionHelper selectionHelperEnd;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_trip);

		mTitleText = (EditText) findViewById(R.id.editTitle);
		locStartEditText = (EditText) findViewById(R.id.editStartLoc);
		locEndEditText = (EditText) findViewById(R.id.editEndLoc);

		selectionHelperStart = new LocationSelectionHelper(
				getApplicationContext());
		selectionHelperEnd = new LocationSelectionHelper(
				getApplicationContext());

		setupListeners();
	}

	/**
	 * persist trip
	 * 
	 * @param v
	 */
	public void onSaveButtonClicked(View v) {

		// start
		try {
			Address a = selectionHelperStart.chosenLocation;
			long loc_id = de.tiidim.roadtrips.Location.createOrFetchLocation(
					a.getLatitude(), a.getLongitude(), a.getLocality());

			chosenStartLocation_id = loc_id;
			locStartEditText = selectionHelperStart.getEditText();
		} catch (NullPointerException e) {
			Toast.makeText(getApplicationContext(),
					R.string.error_loc + " " + R.string.start,
					Toast.LENGTH_LONG).show();
			return;
		}

		// end
		try {
			Address a = selectionHelperEnd.chosenLocation;
			long loc_id = de.tiidim.roadtrips.Location.createOrFetchLocation(
					a.getLatitude(), a.getLongitude(), a.getLocality());

			chosenEndLocation_id = loc_id;
			locEndEditText = selectionHelperEnd.getEditText();
		} catch (NullPointerException e) {
			Toast.makeText(getApplicationContext(),
					R.string.error_loc + " " + R.string.end,
					Toast.LENGTH_LONG).show();
			return;
		}

		Intent intent = setTripProperties(v);

		startActivityForResult(intent, ACTIVITY_LIST);
	}

	/**
	 * helper method that evaluates the form contents
	 * @param v
	 * @return
	 */
	private Intent setTripProperties(View v) {
		Intent intent = new Intent(v.getContext(), TripList.class);

		CustomDateFormat sdf = new CustomDateFormat();
		String plainName = mTitleText.getText().toString();

		DatePicker startPicker = (DatePicker) findViewById(R.id.editStartTime);
		Date startDate = new Date(startPicker.getYear() - 1900,
				startPicker.getMonth(), startPicker.getDayOfMonth());
		String timestart = sdf.format(startDate);

		DatePicker endPicker = (DatePicker) findViewById(R.id.editEndTime);
		Date endDate = new Date(endPicker.getYear() - 1900,
				endPicker.getMonth(), endPicker.getDayOfMonth());
		String timeend = sdf.format(endDate);

		long newTrip = Trip.createTrip(plainName, timestart, timeend);

		long start_id = TripRouting.createRouting(chosenStartLocation_id,
				newTrip, timestart, true);
		long end_id = TripRouting.createRouting(chosenEndLocation_id, newTrip,
				timeend, true);

		Trip.updateInitialTripRouting(newTrip, start_id, end_id);
		return intent;
	}
/**
 * initializes our listeners for the EditText fields
 */
	private void setupListeners() {
		selectionHelperStart.setEditText(locStartEditText);
		selectionHelperEnd.setEditText(locEndEditText);
		
		locStartEditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						selectionHelperStart.reactOnFocusChange(v, hasFocus,
								locStartEditText, v.getContext());

					}
				});

		locEndEditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						selectionHelperEnd.reactOnFocusChange(v, hasFocus,
								locEndEditText, v.getContext());

					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		TripList.mDbHelper.open();
	}

	@Override
	protected void onDestroy() {
		TripList.mDbHelper.close();
		super.onDestroy();
	}
}
