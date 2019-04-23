package com.yellowduck.lending.logic.response;

public class AddingCancellingResponse {

	private final int clientId;
	private final int quantity;
	private String message;

	public AddingCancellingResponse(int clientId, int quantity) {
		this.clientId = clientId;
		this.quantity = quantity;
	}

	public AddingCancellingResponse(int clientId, int quantity, String message) {
		this(clientId, quantity);
		this.message = message;
	}

	public int getClientId() {
		return clientId;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
