package de.tiidim.roadtrips;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Main and Startup Activity Window. Lists all given trips.
 * 
 * @author patrickpermien, stefanhipfel
 */
public class TripList extends ListActivity {
	// define request codes for handling each activityResult (no need for that just yet)
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_VIEW = 1;
	private static final int ACTIVITY_HELP = 2;

	private Cursor mTripsCursor; // used to iterate over the trip search results
	protected static TripsDbAdapter mDbHelper; // handles our local sqlite DB connections.

	/**
	 * fill the list with datasets from sqlite
	 */
	public void fillData() {
		// Get all of the rows from the database and create the item list
		mTripsCursor = Trip.fetchAllTrips();
		startManagingCursor(mTripsCursor);
	
		// Create an array to specify the fields we want to display in the list
		String[] cols = new String[] { "plainName", "startTime", "endTime" };
	
		// and an array of the fields we want to bind those fields to
		int[] views = new int[] { R.id.list_text1, R.id.list_text2,
				R.id.list_text3 };
	
		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter trips = new SimpleCursorAdapter(this,
				R.layout.list_view, mTripsCursor, cols, views);
		setListAdapter(trips);
	
		registerForContextMenu(getListView());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_list);
		mDbHelper = new TripsDbAdapter(this);
		mDbHelper.open();
		fillData();

		PreferenceManager.setDefaultValues(getBaseContext(), R.xml.preferences,
				false);
	}

	/**
	 * event handler redirects to "TripAdd" activity
	 * @param v
	 */
	public void onNewButtonClicked(View v) {
		Intent i = new Intent(this, TripAdd.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	/**
	 * event handler for creating an options menu.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_trip, menu);
		return true;
	}

	/**
	 * event handler for dealing with menu selection
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.Menu_server_con:
			Intent i = new Intent(this, TripPreferences.class);
			startActivityForResult(i, ACTIVITY_CREATE);
			return true;
		case R.id.menu_help:
			Intent help = new Intent(this, Help.class);
			startActivityForResult(help, ACTIVITY_HELP);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * event handler for dealing with context menu selection
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    int index = info.position;
		Log.i("blaaa", index + "");
		boolean success = Trip.deleteTrip(index); 
		if(success) fillData();
		return true;
	}

	/**
	 * event handler for creating a context options menu.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.triplist_context, menu);
	}

	/**
	 * event handler for reacting on any activity result
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	/**
	 * event handler for trip selection from list
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor c = mTripsCursor;
		c.moveToPosition(position);
		Intent i = new Intent(this, TripDetailView.class);
		i.putExtra(TripsDbAdapter.KEY_ROWID, id);
		i.putExtra(Trip.KEY_TRIP_TITLE,
				c.getString(c.getColumnIndexOrThrow(Trip.KEY_TRIP_TITLE)));

		startActivityForResult(i, ACTIVITY_VIEW);
	}

	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}
		

}