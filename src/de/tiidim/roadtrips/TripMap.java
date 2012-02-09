package de.tiidim.roadtrips;

import java.util.List;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Activity for viewing the map with the locations of the selected trip
 * 
 * @author stefanhipfel
 * 
 */
public class TripMap extends MapActivity {

	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;
	Drawable drawable2;
	MapItemizedOverlay itemizedOverlayNight;
	MapItemizedOverlay itemizedOverlayDay;

	OverlayItem overlayItem;
	OverlayItem overlayItem2;

	// customize the APIKEY field to match your API key that you have registered
	// at Google.
	final String APIKEY = "0vZqRFs7SngBbajJ2uyRrBA0itpcBlbZx2MusPQ"; // pp:
																		// 0W56BTCaAfxIWpSceYMdQy8-ajO4erf2kI4Kbcg
	private long trip_id;
	
	
	private GeoPoint middlePoint;
	private MapView map;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		
		// retrieve passed-on param
		Bundle extras = getIntent().getExtras();

		try {
			trip_id = extras.getLong(TripsDbAdapter.KEY_ROWID);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "invalid id",
					Toast.LENGTH_LONG).show();
			return;
		}

		map = new MapView(this, APIKEY);
		map.setClickable(true);
		map.setBuiltInZoomControls(true);
		map.setSatellite(true);
		setContentView(map);

		map.setBuiltInZoomControls(true);

		mapOverlays = map.getOverlays();

		// overlay for stay overnight
		drawable = getResources().getDrawable(R.drawable.marker_overnight);
		itemizedOverlayNight = new MapItemizedOverlay(drawable, map);

		// overlay for stop over
		drawable2 = getResources().getDrawable(R.drawable.marker_day);
		itemizedOverlayDay = new MapItemizedOverlay(drawable2, map);

		Cursor c = TripRouting.fetchTripLocations(trip_id);

		c.moveToFirst();

		int i = 1;

		// only if there cursor has elements
		if (c.getCount() > 1) {

			// calculate the middle point, which will be used to focus the map at
			
			int midPoint = c.getCount() / 2;

			while (i <= c.getCount()) {

				if (c.getString(4).contentEquals("1")) {

					OverlayItem overlayItem = new OverlayItem(getPoint(
							c.getDouble(8), c.getDouble(9)), c.getString(7),
							c.getString(6));

					itemizedOverlayNight.addOverlay(overlayItem);

				} else {

					OverlayItem overlayItem2 = new OverlayItem(getPoint(
							c.getDouble(8), c.getDouble(9)), c.getString(7),
							c.getString(6));
					itemizedOverlayDay.addOverlay(overlayItem2);

				}

				if (midPoint == i) {
					middlePoint = getPoint(c.getDouble(8), c.getDouble(9));
				}
				c.move(1);
				i++;
			}

			// check if overlayitems where created
			if (itemizedOverlayNight.size() > 0) {
				mapOverlays.add(itemizedOverlayNight);
			}
			if (itemizedOverlayDay.size() > 0) {
				mapOverlays.add(itemizedOverlayDay);
			}

			final MapController mc = map.getController();

			mc.animateTo(middlePoint);
			mc.setZoom(7);

		}
		
	}

	/**
	 * Returns a GeoPoint
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	private GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO: Display route in map
		return false;
	}
	


}