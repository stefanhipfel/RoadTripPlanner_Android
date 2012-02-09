package de.tiidim.roadtrips;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;


/**
 * ItemizedOverlay for our TripMap
 * @author stefanhipfel
 *
 */
public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	
/**
 * customized constructor
 * @param defaultMarker graphic derived from drawable
 * @param mapView
 */
	public MapItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker));
		
	} 
	/**
	 * adds the overlay item to the arraylist
	 * @param overlay
	 */

	public void addOverlay(OverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}


}

