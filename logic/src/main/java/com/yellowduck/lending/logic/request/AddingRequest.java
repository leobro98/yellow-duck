package com.yellowduck.lending.logic.request;

public class AddingRequest extends ClientRequest {

	private final int quantity;

	public AddingRequest(int clientId, int quantity) {
		super(clientId);
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}
}
