package de.tiidim.roadtripstests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

/**
 * this is an example to show how we might start to implement the JUnit tests
 * that ship with Android.
 * @author patrickpermien
 *
 */
public class RoadTripPlannerTests extends AndroidTestCase {

	public void testSomething() throws Throwable {
		int one = 1;
		Assert.assertTrue(one + one == 2);
	}
}
