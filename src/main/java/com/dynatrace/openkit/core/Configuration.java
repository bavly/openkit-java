/***************************************************
 * (c) 2016-2017 Dynatrace LLC
 *
 * @author: Christian Schwarzbauer
 */
package com.dynatrace.openkit.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.dynatrace.openkit.core.OpenKitImpl.OpenKitType;
import com.dynatrace.openkit.protocol.HTTPClient;
import com.dynatrace.openkit.protocol.StatusResponse;
import com.dynatrace.openkit.providers.HTTPClientProvider;

/**
 * The Configuration class holds all configuration settings, both provided by the user and the Dynatrace/AppMon server.
 */
public class Configuration {

	private static final boolean DEFAULT_CAPTURE = true;							// default: capture on
	private static final int DEFAULT_SEND_INTERVAL = 2 * 60 * 1000;					// default: wait 2m (in ms) to send beacon
	private static final int DEFAULT_MAX_BEACON_SIZE = 30 * 1024;					// default: max 30KB (in B) to send in one beacon

	// immutable settings
	private final OpenKitType openKitType;
	private final String applicationName;
	private final String applicationID;
	private final long visitorID;
	private final String endpointURL;
	private final boolean verbose;

	// mutable settings
	private AtomicBoolean capture;				// capture on/off; can be written/read by different threads -> atomic
	private int sendInterval;					// beacon send interval; is only written/read by beacon sender thread -> non-atomic
	private String monitorName;					// monitor name part of URL; is only written/read by beacon sender thread -> non-atomic
	private int serverID;						// Server ID (needed for Dynatrace cluster); is only written/read by beacon sender thread -> non-atomic
	private int maxBeaconSize;					// max beacon size; is only written/read by beacon sender thread -> non-atomic

	private final DeviceImpl device;

	private HTTPClient currentHTTPClient;		// current HTTP client (depending on endpoint, monitor, name application ID and server ID)

	private BeaconSender beaconSender;

	private AtomicInteger nextSessionNumber = new AtomicInteger(0);

	// *** constructors ***

	Configuration(String applicationName, String applicationID, long visitorID, String endpointURL, OpenKitType openKitType, boolean verbose) {
		this.verbose = verbose;

		this.openKitType = openKitType;

		// immutable settings
		this.applicationName = applicationName;
		this.applicationID = applicationID;
		this.visitorID = visitorID;
		this.endpointURL = endpointURL;

		// mutable settings
		this.capture = new AtomicBoolean(DEFAULT_CAPTURE);
		this.sendInterval = DEFAULT_SEND_INTERVAL;
		this.monitorName = openKitType.getDefaultMonitorName();
		this.serverID = openKitType.getDefaultServerID();
		this.maxBeaconSize = DEFAULT_MAX_BEACON_SIZE;

		this.device = new DeviceImpl();
	}

	// *** public methods ***

	// initializes the configuration, called by OpenKit.initialize()
	public void initialize() {
		// create beacon sender, but do not start beacon sender thread yet
		beaconSender = new BeaconSender(this);

		// create initial HTTP client
		updateCurrentHTTPClient();

		// block until initial settings are received; initialize beacon sender & starts thread
		beaconSender.initialize();
	}

	// return next session number
	public int createSessionNumber() {
		return nextSessionNumber.incrementAndGet();
	}

	// called when new Session is created, passes Session to beacon sender to start managing it
	public void startSession(SessionImpl session) {
		// if capture is off, there's no need to manage open Sessions
		if (isCapture()) {
			beaconSender.startSession(session);
		}
	}

	// called when Session is ended, passes Session to beacon sender to stop managing it
	public void finishSession(SessionImpl session) {
		// if capture is off, there's no need to manage open and finished Sessions
		if (isCapture()) {
			beaconSender.finishSession(session);
		}
	}

	// updates settings based on a status response
	public final void updateSettings(StatusResponse statusResponse) {
        // if invalid status response OR response code != 200 -> capture off
		if ((statusResponse == null) || (statusResponse.getResponseCode() != 200)) {
			capture.set(false);
		} else {
			capture.set(statusResponse.isCapture());
		}

		// if capture is off -> clear Sessions on beacon sender and leave other settings on their current values
		if (!isCapture()) {
			beaconSender.clearSessions();
			return;
		}

		// use monitor name from beacon response or default
		String newMonitorName = statusResponse.getMonitorName();
		if (newMonitorName == null) {
			newMonitorName = openKitType.getDefaultMonitorName();
		}

		// use server id from beacon response or default
		int newServerID = statusResponse.getServerID();
		if (newServerID == -1) {
			newServerID = openKitType.getDefaultServerID();
		}

		// check if URL changed
		boolean urlChanged = false;
		if (!monitorName.equals(newMonitorName)) {
			monitorName = newMonitorName;
			urlChanged = true;
		}
		if (serverID != newServerID) {
			serverID = newServerID;
			urlChanged = true;
		}

		// URL changed -> HTTP client has to be updated
		if (urlChanged) {
			updateCurrentHTTPClient();
		}

		// use send interval from beacon response or default
		int newSendInterval = statusResponse.getSendInterval();
		if (newSendInterval == -1) {
			newSendInterval = DEFAULT_SEND_INTERVAL;
		}
		// check if send interval has to be updated
		if (sendInterval != newSendInterval) {
			sendInterval = newSendInterval;
		}

		// use max beacon size from beacon response or default
		int newMaxBeaconSize = statusResponse.getMaxBeaconSize();
		if (newMaxBeaconSize == -1) {
			newMaxBeaconSize = DEFAULT_MAX_BEACON_SIZE;
		}
		if (maxBeaconSize != newMaxBeaconSize) {
			maxBeaconSize = newMaxBeaconSize;
		}
	}

	// shut down configuration -> shut down beacon sender
	public void shutdown() {
		if (beaconSender != null) {
			beaconSender.shutdown();
		}
	}

	// *** private methods ***

	// create new HTTP client and updates the current HTTP client
	private void updateCurrentHTTPClient() {
		this.currentHTTPClient = HTTPClientProvider.createHTTPClient(createBaseURL(), applicationID, serverID, verbose);
	}

	// create base URL for HTTP client from endpoint & monitorname
	private String createBaseURL() {
		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(endpointURL);
		if (!endpointURL.endsWith("/") && !monitorName.startsWith("/")) {
			urlBuilder.append('/');
		}
		urlBuilder.append(monitorName);

		return urlBuilder.toString();
    }

	// *** getter methods ***

	public boolean isDynatrace() {
		return openKitType == OpenKitType.DYNATRACE;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getApplicationID() {
		return applicationID;
	}

	public long getVisitorID() {
		return visitorID;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public boolean isCapture() {
		return capture.get();
	}

	public int getSendInterval() {
		return sendInterval;
	}

	public int getMaxBeaconSize() {
		return maxBeaconSize;
	}

	public DeviceImpl getDevice() {
		return device;
	}

	public HTTPClient getCurrentHTTPClient() {
		return currentHTTPClient;
	}

}
