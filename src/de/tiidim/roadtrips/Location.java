package de.tiidim.roadtrips;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * bundles our 
 * @author patrickpermien
 *
 */
public class Location {
	// column names used for DB queries
	private static final String KEY_LOCATION_LATITUDE = "latitude";
	private static final String KEY_LOCATION_LONGITUDE = "longitude";
	private static final String KEY_LOCATION_PLAINNAME = "plainName";

	/**
	 * this wrapper method tries to get a location from database; creates and
	 * persists it if nothing was found.
	 * 
	 * @param latitude
	 *            for geo location coding API.
	 * @param longitude
	 *            for geo location coding API.
	 * @param plainName
	 *            name string.
	 * @return row_id of Location
	 */
	public static long createOrFetchLocation(double latitude, double longitude,
			String plainName) {
		Cursor c = fetchLocation(latitude, longitude);
		if (0 == c.getCount()) {
			return createLocation(latitude, longitude, plainName);
		}
		c.moveToFirst();
		return c.getLong(c.getColumnIndex(TripsDbAdapter.KEY_ROWID));
	}

	/**
	 * persists a new location in the database.
	 **/
	private static long createLocation(double latitude, double longitude,
			String plainName) {
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_LOCATION_LATITUDE, latitude);
		initialValues.put(KEY_LOCATION_LONGITUDE, longitude);
		initialValues.put(KEY_LOCATION_PLAINNAME, plainName);

		return TripsDbAdapter.mDb.insert(TripsDbAdapter.TABLE_LOCATION, null,
				initialValues);
	}

	/**
	 * fetch single location by id
	 * 
	 * @param rowId
	 * @return
	 */
	public Cursor fetchLocation(long rowId) {
		return fetchLocation(TripsDbAdapter.KEY_ROWID + " = ? ",
				new String[] { rowId + "" });
	}

	/**
	 * fetch single location by coordinates
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static Cursor fetchLocation(double latitude, double longitude) {
		return fetchLocation(KEY_LOCATION_LATITUDE + " = ? AND "
				+ KEY_LOCATION_LONGITUDE + " = ?", new String[] {
				latitude + "", longitude + "" });
	}

	/**
	 * retrieves a single location from the database, if possible.
	 **/
	private static Cursor fetchLocation(String condition,
			String[] conditionParams) {
		Cursor c = TripsDbAdapter.mDb.query(true,
				TripsDbAdapter.TABLE_LOCATION, null, condition,
				conditionParams, null, null, null, "0,1");
		return c;
	}
}
