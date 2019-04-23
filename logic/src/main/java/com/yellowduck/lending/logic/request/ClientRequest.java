package com.yellowduck.lending.logic.request;

public class ClientRequest implements InternalRequest {

	protected final int clientId;

	public ClientRequest(int clientId) {
		this.clientId = clientId;
	}

	public int getClientId() {
		return clientId;
	}
}
