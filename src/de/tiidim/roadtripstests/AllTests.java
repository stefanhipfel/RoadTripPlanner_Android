package de.tiidim.roadtripstests;

import junit.framework.TestSuite;


import junit.framework.Test;
import android.test.suitebuilder.TestSuiteBuilder;


/**
 * A test suite containing all tests for my application.
 * @see http://marakana.com/tutorials/android/junit-test-example.html 
 */
public class AllTests extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }
}