package de.tiidim.roadtrips;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Reveals details about a given trip.
 * 
 * @author patrickpermien, stefanhipfel
 * @see Trip
 */
public class TripDetailView extends ListActivity {

	private Cursor mLocationsCursor;
	private Cursor c;
	//TripUpload upload = new TripUpload();
	private Bundle extras;
	private Long trip_id;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		TripList.mDbHelper.open();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tripdetailview);

		// retrieve passed-on param
		extras = getIntent().getExtras();

		// define desired trip. This uses plain name and ID as fallback, 
		// until we receive intelligence on how to get back our sqlite ID from the list cursor. 

		try {
			c = Trip.getTripByName(extras.getString("plainName"));
			trip_id = extras.getLong(TripsDbAdapter.KEY_ROWID);
		} catch (Exception e) {
			try {
				c = Trip.getTripById(extras);
			}
			catch(Exception e2) {
				Toast.makeText(getApplicationContext(), "invalid identifier",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		// fill basic details (trip`s title and schedule)
		c.moveToFirst();
		String title = TripsDbAdapter.getStringValueFromColumn(
				Trip.KEY_TRIP_TITLE, c);
		fillTextViewFromString(title, R.id.TextViewPlainName);

		// CustomDateFormat cdf = new CustomDateFormat();

		String timestart = TripsDbAdapter.getStringValueFromColumn(
				Trip.KEY_TRIP_TIMESTART, c);
		//
		fillTextViewFromString(timestart, R.id.TextViewStartTime);

		String timeend = TripsDbAdapter.getStringValueFromColumn(
				Trip.KEY_TRIP_TIMEEND, c);
		fillTextViewFromString(timeend, R.id.TextViewEndTime);

		// fill locations list:
		fillLocations(extras.getLong(TripsDbAdapter.KEY_ROWID));
		
	}
	
	

	/**
	 * inflates our menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_tripdetail, menu);
		return true;
	}

	/**
	 * persists the Trip in the database.
	 * 
	 * @param v
	 *            the sender view
	 */
	public void onAddButtonClicked(View v) {
		// define desired trip

		String id = TripsDbAdapter.getStringValueFromColumn(TripsDbAdapter.KEY_ROWID, c);
		long rowid = Long.parseLong(id);
		Intent myIntent = new Intent(v.getContext(), AddLocationToTrip.class);
		myIntent.putExtra(TripsDbAdapter.KEY_ROWID, rowid);
		startActivityForResult(myIntent, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_home:
			/**
			 * redirects back to home.
			 * 
			 * @param v
			 *            the sender view
			 */
			Intent i = new Intent(this, TripList.class);
			startActivityForResult(i, 0);
			return true;
		case R.id.menu_map:
			/**
			 * go to google maps
			 * 
			 * @param v
			 *            the sender view
			 */
			
			String id = TripsDbAdapter.getStringValueFromColumn(TripsDbAdapter.KEY_ROWID, c);
			long rowid = Long.parseLong(id);
			Intent mapIntent = new Intent(this, TripMap.class);
			mapIntent.putExtra(TripsDbAdapter.KEY_ROWID, rowid);
			startActivityForResult(mapIntent, 0);

			return true;
		case R.id.menu_upload:
			try {
				uploadTrip();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * uploads data to the associated web server, in the background.
	 * 
	 * @param v
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void uploadTrip() throws JSONException, ClientProtocolException,
			IOException {

		Context context = this;
		new TripUpload(context, trip_id).execute(context);
		
		
		
	}

	/**
	 * fills the list view.
	 * 
	 * @param tripId
	 */
	private void fillLocations(long tripId) {
		mLocationsCursor = TripRouting.fetchTripLocations(tripId);
		startManagingCursor(mLocationsCursor);

		// Create an array to specify the fields we want to display in the list
		String[] cols = new String[] { "plainName", "arrivalTime", "stayOvernight"};

		// and an array of the fields we want to bind those fields to
		int[] views = new int[] { R.id.list_locationname,
				R.id.list_locationarrivaltime, R.id.list_icon};				
		

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter locations = new SimpleCursorAdapter(this,
				R.layout.tripdetails, mLocationsCursor, cols, views);
		locations.setViewBinder(new TripDetailViewBinder()); 
			
		
		setListAdapter(locations);
	}


	/**
	 * fills text view from given column.
	 * 
	 * @param value
	 *            input
	 * @param indexName
	 *            column name
	 * @param viewId
	 *            target
	 */
	private void fillTextViewFromString(String value, int viewId) {
		View v = findViewById(viewId);
		TextView tv = (TextView) v;
		tv.append(value);
	}


	@Override
	protected void onDestroy() {
		TripList.mDbHelper.close();
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		TripList.mDbHelper.open();
	}
	

}

