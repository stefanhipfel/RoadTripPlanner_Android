package de.tiidim.roadtrips;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * 
 * bundles our functionality for binding locations to trips.
 * @author stefanhipfel, patrickpermien
 */
public class TripRouting {
	// column names used for DB queries
	private static final String KEY_TRIPROUTING_FKTRIP = "trip_id";
	private static final String KEY_TRIPROUTING_FKLOCATION = "location_id";
	private static final String KEY_TRIPROUTING_ARRIVALTIME = "arrivalTime";
	private static final String KEY_TRIPROUTING_STAYOVERNIGHT = "stayOvernight";

	/**
	 * creates and persists a new routing entry.
	 * 
	 * @param chosenLocation_id
	 *            foreign key reference
	 * @param trip_id
	 *            foreign key reference
	 * @param arrivalTime
	 *            datetime
	 * @param stayOvernight
	 *            boolean flag
	 * @return
	 */
	public static long createRouting(long chosenLocation_id, long trip_id,
			String arrivalTime, boolean stayOvernight) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TRIPROUTING_FKTRIP, trip_id);
		initialValues.put(KEY_TRIPROUTING_FKLOCATION, chosenLocation_id);
		initialValues.put(KEY_TRIPROUTING_ARRIVALTIME, arrivalTime);
		initialValues.put(KEY_TRIPROUTING_STAYOVERNIGHT, stayOvernight);

		return TripsDbAdapter.mDb.insert(TripsDbAdapter.TABLE_TRIPROUTING,
				null, initialValues);
	}

	/**
	 * deletes a given routing entry.
	 * 
	 * @param rowId
	 *            unique identifier of the entry
	 * @return boolean success flag
	 */
	static boolean deleteTripRouting(long rowId) {
		return TripsDbAdapter.mDb.delete(TripsDbAdapter.TABLE_TRIPROUTING,
				TripsDbAdapter.KEY_ROWID + " = ?", new String[] { rowId + "" }) > 0;
	}

	/**
	 * Gets all locations that are associated with a given trip.
	 * 
	 * @param tripId
	 *            the trip identifier.
	 * @return a cursor with which you can iterate over the results
	 * @throws SQLException
	 */
	public static Cursor fetchTripLocations(long tripId) throws SQLException {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(TripsDbAdapter.TABLE_TRIPROUTING + ", "
				+ TripsDbAdapter.TABLE_LOCATION);
		builder.setTables(TripsDbAdapter.TABLE_TRIPROUTING
				+ " LEFT OUTER JOIN " + TripsDbAdapter.TABLE_LOCATION + " ON ("
				+ TripsDbAdapter.TABLE_TRIPROUTING
				+ ".location_id = Location._id) ");

		Cursor c = builder.query(TripsDbAdapter.mDb, null,
				KEY_TRIPROUTING_FKTRIP + " = " + tripId, null, null, null,
				KEY_TRIPROUTING_ARRIVALTIME + " ASC");

		return c;
	}

	/**
	 * deletes all routing entries of a given trip. (Does not touch the
	 * locations that are associated with them.)
	 * 
	 * @param rowId
	 *            Trip ID
	 * @return success flag
	 */
	public static boolean deleteAllRoutings(long rowId) {
		return TripsDbAdapter.mDb.delete(TripsDbAdapter.TABLE_TRIPROUTING,
				TripRouting.KEY_TRIPROUTING_FKTRIP + " = " + rowId, null) > 0;
	}
}
