package com.yellowduck.lending.data;

import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.response.QueueEntry;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simplest implementation of the data source API. The data are stored in the in-memory structure.
 */
@Repository
public class MapDataSource implements LendingDataSource {

	Map<Integer, QueueEntry> orders;

	MapDataSource() {
		orders = new HashMap<>();
	}

	@Override
	public void addOrder(int clientId, int quantity) {
		orders.put(clientId, new QueueEntry(clientId, quantity, LocalDateTime.now()));
	}

	@Override
	public QueueEntry getOrder(int clientId) {
		return orders.get(clientId);
	}

	@Override
	public List<QueueEntry> getAllOrders() {
		List<QueueEntry> queue = new ArrayList<>();

		for (Integer clientId : orders.keySet()) {
			QueueEntry order = orders.get(clientId);
			QueueEntry entry = new QueueEntry(clientId, order.getQuantity(), order.getSubmissionTime());
			queue.add(entry);
		}
		return queue.stream()
				.sorted(Comparator.comparing(QueueEntry::getSubmissionTime))
				.collect(Collectors.toList());
	}

	@Override
	public QueueEntry cancelOrder(int clientId) {
		return orders.remove(clientId);
	}
}
