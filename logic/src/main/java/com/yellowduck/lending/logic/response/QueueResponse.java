package com.yellowduck.lending.logic.response;

import java.util.ArrayList;
import java.util.List;

public class QueueResponse {

	private List<QueueEntry> entries;

	public QueueResponse() {
		entries = new ArrayList<>();
	}

	public QueueResponse(List<QueueEntry> entries) {
		this.entries = entries;
	}

	public List<QueueEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<QueueEntry> entries) {
		this.entries = entries;
	}
}
