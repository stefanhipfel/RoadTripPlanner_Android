package de.tiidim.roadtrips;

import java.text.SimpleDateFormat;

/**
 * customizes SimpleDateFormat a little. We might want to transform this into a config issue later. 
 * @author patrickpermien
 *
 */
public class CustomDateFormat extends SimpleDateFormat {
	private static final long serialVersionUID = 1L;

	/** default constructor */
	public CustomDateFormat() {
		super.applyPattern("yyyy-MM-dd");
	}
}
