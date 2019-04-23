package com.yellowduck.lending.logic.response;

import java.util.ArrayList;
import java.util.List;

public class InternalResponse {

	private List<String> messages;
	private Object payload;
	private ResultType result;

	public enum ResultType {
		OK,
		ERROR,
		FATAL;
	}

	public InternalResponse(ResultType result) {
		this.result = result;
		messages = new ArrayList<>();
	}

	public InternalResponse(ResultType result, Object payload) {
		this(result);
		this.payload = payload;
	}

	public InternalResponse(ResultType result, List<String> messages) {
		this(result);
		this.messages = messages;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public ResultType getResult() {
		return result;
	}

	public void setResult(ResultType result) {
		this.result = result;
	}
}
