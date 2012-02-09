package de.tiidim.roadtrips;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/***
 * Activity that demonstrates in-app help content capabilities
 * @author patrickpermien
 */
public class Help extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		TextView myText = (TextView) findViewById(R.id.helpviewtext);
		myText.setText("Help is on the way.");
	}
}
