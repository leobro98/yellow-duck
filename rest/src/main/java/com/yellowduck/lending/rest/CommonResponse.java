package com.yellowduck.lending.rest;

import java.util.ArrayList;
import java.util.List;

public class CommonResponse {

	private List<String> messages;
	private Object payload;

	CommonResponse() {
		messages = new ArrayList<>();
	}

	CommonResponse(Object payload) {
		this();
		this.payload = payload;
	}

	public List<String> getMessages() {
		return messages;
	}

	void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public Object getPayload() {
		return payload;
	}

	void setPayload(Object payload) {
		this.payload = payload;
	}

	void addMessage(String message) {
		messages.add(message);
	}
}
