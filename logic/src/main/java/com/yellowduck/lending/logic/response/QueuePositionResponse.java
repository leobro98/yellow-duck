package com.yellowduck.lending.logic.response;

import java.time.Duration;

public class QueuePositionResponse {

	private final int clientId;
	private final int position;
	private final Duration waitingTime;

	public QueuePositionResponse(int clientId, int position, Duration waitingTime) {
		this.clientId = clientId;
		this.position = position;
		this.waitingTime = waitingTime;
	}

	public int getClientId() {
		return clientId;
	}

	public int getPosition() {
		return position;
	}

	public Duration getWaitingTime() {
		return waitingTime;
	}
}
