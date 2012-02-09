package de.tiidim.roadtrips;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
/**
 * Bundles the logic around a trip in static methods.
 * We do not create objects for every trip. 
 * We do this on purpose to reduce code overhead, 
 * in this small-sized project. 
 * @author patrickpermien
 *
 */
public class Trip {
	// column names used for DB queries
	public static final String KEY_TRIP_TITLE = "plainName";
	public static final String KEY_TRIP_TIMESTART = "startTime";
	public static final String KEY_TRIP_TIMEEND = "endTime";
	public static final String KEY_TRIP_LOCSTART = "startLocation_id";
	public static final String KEY_TRIP_LOCEND = "endLocation_id";

	/**
	 * gets the row_id from the bundle and forwards the request to db helper.
	 * 
	 * @param extras
	 *            Request bundle
	 * @return Cursor
	 */
	public static Cursor getTripById(Bundle extras) {
		long id = extras.getLong(TripsDbAdapter.KEY_ROWID);
		Cursor c = Trip.fetchTrip(id);
		return c;
	}

	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	public static long createTrip(String title, String timestart, String timeend) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TRIP_TITLE, title);
		initialValues.put(KEY_TRIP_TIMESTART, timestart);
		initialValues.put(KEY_TRIP_TIMEEND, timeend);

		return TripsDbAdapter.mDb.insert(TripsDbAdapter.TABLE_TRIP, null,
				initialValues);
	}

	/**
	 * Delete the trip with the given rowId
	 * 
	 * @param rowId
	 *            id of trip to delete
	 * @return true if deleted, false otherwise
	 */
	public static boolean deleteTrip(long rowId) {
		//Cursor c = TripRouting.fetchTripLocations(rowId);
		TripRouting.deleteAllRoutings(rowId);

		return TripsDbAdapter.mDb.delete(TripsDbAdapter.TABLE_TRIP,
				TripsDbAdapter.KEY_ROWID + " = ?", new String[] { rowId + "" }) > 0;
	}

	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId
	 *            id of trip to update
	 * @param editPlainName
	 *            value to set trip title to
	 * @param timestart
	 *            value to set trip start // TODO
	 * @return true if the trip was successfully updated, false otherwise
	 */
	public static boolean updateTripName(long rowId, String editPlainName) {// ,
																			// String
		// timestart)
		// {
		ContentValues args = new ContentValues();
		args.put(Trip.KEY_TRIP_TITLE, editPlainName);
		// args.put(KEY_TRIP_TIMESTART, timestart);

		return TripsDbAdapter.mDb.update(TripsDbAdapter.TABLE_TRIP, args,
				TripsDbAdapter.KEY_ROWID + "=" + rowId, null) > 0;
	}

	public static boolean updateInitialTripRouting(long trip_id, long locstart,
			long locend) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TRIP_LOCSTART, locstart);
		initialValues.put(KEY_TRIP_LOCEND, locend);
		return TripsDbAdapter.mDb.update(TripsDbAdapter.TABLE_TRIP,
				initialValues, TripsDbAdapter.KEY_ROWID + "=" + trip_id, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all trips in the database
	 * 
	 * @return Cursor over all trips
	 */
	public static Cursor fetchAllTrips() {
		return TripsDbAdapter.mDb
				.query(TripsDbAdapter.TABLE_TRIP, new String[] { "_id",
						Trip.KEY_TRIP_TITLE, "startTime", "endTime" }, null,
						null, null, null, " startTime DESC ");
	}

	/**
	 * Return a Cursor positioned at the dataset that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public static Cursor fetchTrip(long rowId) throws SQLException {
		Cursor c = TripsDbAdapter.mDb.query(true, TripsDbAdapter.TABLE_TRIP,
				null, TripsDbAdapter.KEY_ROWID + " = ?", new String[] { rowId
						+ "" }, null, null, null, null);
		return c;

	}
	/**
	 * Return a Cursor positioned at the dataset that matches the given plainName
	 * @param plainName
	 * @return
	 */
	public static Cursor getTripByName(String plainName) {
		Cursor c = TripsDbAdapter.mDb.query(true, TripsDbAdapter.TABLE_TRIP,
				null, Trip.KEY_TRIP_TITLE + " = ?",
				new String[] { plainName }, null, null,
				null, null);
		return c;
	}
}
