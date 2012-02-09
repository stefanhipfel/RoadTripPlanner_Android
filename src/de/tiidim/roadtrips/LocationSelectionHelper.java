package de.tiidim.roadtrips;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * helper class that handles the refinement process of the user's location
 * inputs
 * 
 * @author stefanhipfel, patrickpermien
 * 
 */
public class LocationSelectionHelper {

	private EditText editText; // to-be-referenced UI element

	/**
	 * getter
	 * 
	 * @return
	 */

	protected EditText getEditText() {
		return editText;
	}

	/**
	 * setter
	 * 
	 * @param editText
	 *            value
	 */
	public void setEditText(EditText editText) {
		this.editText = editText;
	}

	private Context applicationContext; // we must reference the context in
										// which the GUI action takes place
	private AlertDialog.Builder builder; // helps us construct the choice panel
	private Geocoder geocoder; // gives us google API access

	protected Address chosenLocation; // caches the selection

	final int maxResult = 5; // config how many results we want to receive from
								// geolocation at maximum
	Address[] possibleAddresses = new Address[maxResult]; // possible locations
															// as found by
															// geolocation API

	/**
	 * constructor. Make sure to pass the correct context here, which most
	 * likely is your activity context!
	 * 
	 * @param context
	 */
	protected LocationSelectionHelper(Context context) {
		this.applicationContext = context;
		geocoder = new Geocoder(applicationContext);
	}

	/**
	 * check location for existence; show alert in case of failure or several
	 * results.
	 * 
	 * @param userInput
	 * @param v
	 * @param context
	 *            Make sure to pass the correct context here, which is related
	 *            to your target view!
	 * @return
	 * @throws IOException
	 */
	private void checkUserInput(EditText userInput, View v, Context context)
			throws IOException {
		if (null == userInput)
			return;

		String city = userInput.getText().toString();
		List<Address> possibleLocations = geocoder.getFromLocationName(city,
				maxResult);

		if (possibleLocations.isEmpty()) {
			// uh oh - should not happen!
			Toast.makeText(context, R.string.error_loc_not_found, Toast.LENGTH_LONG);
		} else if (1 == possibleLocations.size()) {
			String result = possibleLocations.get(0).getLocality();
			possibleAddresses = new Address[] { possibleLocations.get(0) };
			chosenLocation = possibleAddresses[0];
			if (null != editText) {
				editText.setText(result);
			}

		} else {
			CharSequence[] items = new CharSequence[possibleLocations.size()];
			for (int i = 0; i < possibleLocations.size(); i++) {
				Address a = possibleLocations.get(i);
				items[i] = a.getLocality() + ", " + a.getCountryName();
				try {
					possibleAddresses[i] = a;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
			createAlertBox(items, context);

			builder.show();
		}
	}

	/**
	 * instantiates an alert box that can be used for choosing the correct
	 * location in case of ambiguities
	 * 
	 * @param items
	 *            the selectable options
	 * @param context
	 */
	private void createAlertBox(final CharSequence[] items, Context context) {
		builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.error_loc_select);
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						if (null == editText) {
							throw new NullPointerException("editText was not assigned");
						} else {
							chosenLocation = possibleAddresses[item];
							System.out.println(possibleAddresses.toString());
							editText.setText(items[item]);
						}
						dialog.dismiss();
					}
				});
	}

	/**
	 * event handler content
	 * 
	 * @param v
	 *            view
	 * @param hasFocus
	 *            whether focus was gained (true) or lost.
	 * @param eventSource
	 *            where the event was triggered.
	 * @param context
	 * @return
	 */
	protected void reactOnFocusChange(View v, boolean hasFocus,
			EditText eventSource, Context context) {
		if (!hasFocus) { // the editText has just been left
			try {
				editText = eventSource; // remember which field the
										// event came from!
				checkUserInput(eventSource, v, context);
			} catch (IOException e) {
				Toast.makeText(context, "uh-oh", // d`oh
						Toast.LENGTH_LONG);
				e.printStackTrace();
			}
		}
	}

}
