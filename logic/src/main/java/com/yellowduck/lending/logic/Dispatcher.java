package com.yellowduck.lending.logic;

import com.yellowduck.lending.logic.response.QueueEntry;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Most of business logic resides here. Contains plugged-in strategy for prioritization clients' orders.
 */
@Component
public class Dispatcher {

	// different strategies to prioritize clients can be used; this member gets injected one of the strategies
	private PriorityStrategy queueStrategy;

	static final int MAX_PREMIUM_CLIENT_ID = 1000;
	public static final int MAX_CART_LOAD = 25;
	private static final int MINUTES_BETWEEN_DELIVERIES = 5;

	@Autowired
	public Dispatcher(PriorityStrategy queueStrategy) {
		this.queueStrategy = queueStrategy;
	}

	/**
	 * Given the list of the orders in the order of their submission, picks several of them for the inclusion
	 * into the next delivery, taking the priority of clients into account.
	 *
	 * @param rawQueue chronological list of all orders.
	 * @return list of orders to be included into the next delivery.
	 */
	public List<QueueEntry> getNextDelivery(List<QueueEntry> rawQueue) {
		List<QueueEntry> queue = getPrioritizedQueue(rawQueue);
		int cartLoad = 0;
		List<QueueEntry> delivery = new ArrayList<>();

		// Joe's cart is able to carry 25 rubber ducks and he should put as many orders into his cart without
		// skipping, changing or splitting any orders
		for (QueueEntry entry : queue) {
			if (fitsInCart(entry, cartLoad)) {
				cartLoad += entry.getQuantity();
				delivery.add(entry);
			} else {
				break;
			}
		}

		return delivery;
	}

	/**
	 * Finds and returns the client's order updated with the order's position in the queue and approximate waiting time.
	 *
	 * @param clientId client ID,
	 * @param rawQueue all orders sorted by the time of their submission.
	 * @return order of the client with the given ID.
	 */
	public QueueEntry getPositionedEntry(int clientId, List<QueueEntry> rawQueue) {
		List<QueueEntry> queue = getPrioritizedQueue(rawQueue);
		return queue.stream()
				.filter(x -> x.getClientId() == clientId)
				.findFirst()
				.orElse(null);
	}

	/**
	 * Re-orders client orders according to the prioritization strategy and updates them with the order's position
	 * in the queue and approximate waiting time.
	 *
	 * @param rawQueue all orders sorted by the time of their submission.
	 * @return orders queue sorted according to their submission time and priority.
	 */
	public List<QueueEntry> getPrioritizedQueue(List<QueueEntry> rawQueue) {
		prioritizeClients(rawQueue);
		List<QueueEntry> queue = queueStrategy.getPrioritizedQueue(rawQueue);
		updateQueueEntries(queue);
		return queue;
	}

	private void prioritizeClients(List<QueueEntry> rawQueue) {
		for (QueueEntry entry : rawQueue) {
			if (entry.getClientId() < MAX_PREMIUM_CLIENT_ID) {
				entry.setClientType(QueueEntry.ClientType.PREMIUM);
			} else {
				entry.setClientType(QueueEntry.ClientType.NORMAL);
			}
		}
	}

	private void updateQueueEntries(List<QueueEntry> queue) {
		int position = 1;
		int cartLoad = 0;
		Duration waitingTime = Duration.ZERO;

		for (QueueEntry entry : queue) {
			if (!fitsInCart(entry, cartLoad)) {
				// "fill" the next cart
				cartLoad = 0;
				waitingTime = waitingTime.plusMinutes(MINUTES_BETWEEN_DELIVERIES);
			}

			cartLoad += entry.getQuantity();
			entry.setPosition(position++);
			entry.setWaitingTime(waitingTime);
		}
	}

	private boolean fitsInCart(QueueEntry entry, int cartLoad) {
		return cartLoad + entry.getQuantity() <= MAX_CART_LOAD;
	}
}
