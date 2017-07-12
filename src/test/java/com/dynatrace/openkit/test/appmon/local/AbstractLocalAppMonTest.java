/***************************************************
 * (c) 2016-2017 Dynatrace LLC
 *
 * @author: Christian Schwarzbauer
 */
package com.dynatrace.openkit.test.appmon.local;

import java.util.Random;

import org.junit.After;
import org.junit.Before;

import com.dynatrace.openkit.test.AbstractAppMonTest;
import com.dynatrace.openkit.test.OpenKitTestFactory;
import com.dynatrace.openkit.test.TestConfiguration;

public abstract class AbstractLocalAppMonTest extends AbstractAppMonTest {

	protected TestConfiguration testConfiguration = null;

	public AbstractLocalAppMonTest() {
		testConfiguration = new TestConfiguration();
		testConfiguration.setVisitorID(new Random(System.currentTimeMillis()).nextLong());
		testConfiguration.setStatusResponse("type=m&si=120&bn=dynaTraceMonitor&id=1", 200);
	}

	@Before
	public void setup() {
		openKitTestImpl = OpenKitTestFactory.createAppMonLocalInstance(TEST_APPLICATION_NAME, TEST_APPLICATION_ID, TEST_ENDPOINT, testConfiguration);
		openKit = openKitTestImpl;
		openKit.initialize();
	}

	@Before
	public void printStart() {
		System.out.println("Local AppMon Test: " + this.getClass().getSimpleName() + " - Start");
	}

	@After
	public void printEnd() {
		System.out.println("Local AppMon Test: " + this.getClass().getSimpleName() + " - End");
		openKit.shutdown();
	}

}