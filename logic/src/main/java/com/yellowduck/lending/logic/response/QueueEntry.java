package com.yellowduck.lending.logic.response;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * It is the type for passing data both between data layer and business logic layer
 * and between business logic layer and REST layer.
 * In production you would normally have different types, but left here for simplicity.
 */
public class QueueEntry {

	private final int clientId;
	private int quantity;
	private final LocalDateTime submissionTime;
	private ClientType clientType;
	private Integer position;
	private Duration waitingTime;

	public enum ClientType {
		PREMIUM,
		NORMAL
	}

	public QueueEntry(int clientId, int quantity, LocalDateTime submissionTime) {
		this.clientId = clientId;
		this.quantity = quantity;
		this.submissionTime = submissionTime;
	}

	public int getClientId() {
		return clientId;
	}

	public int getQuantity() {
		return quantity;
	}

	public LocalDateTime getSubmissionTime() {
		return submissionTime;
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Duration getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(Duration waitingTime) {
		this.waitingTime = waitingTime;
	}
}
