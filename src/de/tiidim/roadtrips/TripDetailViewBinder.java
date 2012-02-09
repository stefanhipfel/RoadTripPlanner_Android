package de.tiidim.roadtrips;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * Changed ViewBinder class for the TripDetailView, to get control over the
 * ImageView (change resources of ImageView)
 * 
 * @author stefanhipfel
 * @see TripDetailView
 */
public class TripDetailViewBinder implements ViewBinder {
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		int nImageIndex = cursor.getColumnIndex("stayOvernight");
		if (nImageIndex == columnIndex) {
			// Change the image resource if stayOvernight is true
			ImageView typeControl = (ImageView) view;
			if (cursor.getString(columnIndex).contentEquals("1")) {
				typeControl.setImageResource(R.drawable.stay_overnight_icon);

			} else {
				// point of interest
				typeControl.setImageResource(R.drawable.poi_icon);
			}
			return true;
		}
		return false;
	}
}
