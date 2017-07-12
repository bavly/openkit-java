/***************************************************
 * (c) 2016-2017 Dynatrace LLC
 *
 * @author: Christian Schwarzbauer
 */
package com.dynatrace.openkit.core;

import com.dynatrace.openkit.api.OpenKit;
import com.dynatrace.openkit.protocol.Beacon;

/**
 * Inherited class of {@link WebRequestTagBaseImpl} which can be used for tagging and timing of a web request handled by any 3rd party HTTP Client.
 * Setting the Dynatrace tag to the {@link OpenKit.WEBREQUEST_TAG_HEADER} HTTP header has to be done manually by the user.
 */
public class WebRequestTagStringURL extends WebRequestTagBaseImpl {

	// *** constructors ***

	// creates web request tag with a simple string URL
	public WebRequestTagStringURL(Beacon beacon, ActionImpl action, String url) {
		super(beacon, action);

		// separate query string from URL
		if (url != null) {
			this.url = url.split("\\?")[0];
		}
	}

}
