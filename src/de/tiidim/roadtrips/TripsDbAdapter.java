package de.tiidim.roadtrips;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * handles connections to our local sqlite database
 * 
 * @author patrickpermien
 * 
 */
public class TripsDbAdapter {
	private static final String TAG = "TripsDbAdapter";
	private DatabaseHelper mDbHelper;
	static SQLiteDatabase mDb;
	private static final int DATABASE_VERSION = 2;
	private final Context mCtx;
	
	/**
	 * Database creation sql statement
	 */
	// table and column names used for DB queries
	static final String TABLE_LOCATION = "Location";
	protected static final String TABLE_TRIP = "Trip";

	static final String TABLE_TRIPROUTING = "TripRouting";

	public static final String KEY_ROWID = "_id";

	// stored DDL queries
	private static final String LOCATION_TABLE_CREATE = "CREATE TABLE \"Location\" (\"_id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"plainName\" VARCHAR NOT NULL ,\"remark\" VARCHAR, \"latitude\" DOUBLE, \"longitude\" DOUBLE)";
	private static final String TRIP_TABLE_CREATE = "CREATE TABLE \"Trip\" (\"_id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"plainName\" VARCHAR NOT NULL ,\"startTime\" DATETIME NOT NULL ,\"endTime\" DATETIME NOT NULL ,\"startLocation_id\" INTEGER,\"endLocation_id\" INTEGER)";
	private static final String TRIPROUTING_TABLE_CREATE = "CREATE TABLE \"TripRouting\" (\"_id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"location_id\" INTEGER NOT NULL , \"trip_id\" INTEGER NOT NULL , \"arrivalTime\" DATETIME NOT NULL,\"stayOvernight\" BOOL NOT NULL  )";
	private static final String DATABASE_NAME = "roadtrips";

	/** custom constructor */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(LOCATION_TABLE_CREATE);
			db.execSQL(TRIP_TABLE_CREATE);
			db.execSQL(TRIPROUTING_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS roadtrips");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public TripsDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public TripsDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		// insert logic here to add sample datasets as necessary
		return this;
	}

	/** closes the sqlite connection. */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * gets a string value of a database column "columnName" by using a given
	 * cursor
	 * 
	 * @param columnName
	 * @param c
	 * @return
	 */
	public static String getStringValueFromColumn(String columnName, Cursor c) {
		int colIndex = c.getColumnIndex(columnName);
		return c.getString(colIndex);
	}

}
