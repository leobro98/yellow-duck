package com.yellowduck.lending.logic;

import com.yellowduck.lending.logic.response.QueueEntry;
import java.util.List;

/**
 * "Orders from premium customers always have a higher priority than normal customers" - is not clear at all.
 * Different strategies to prioritize customers can be used. That's why iterchangeable strategy class is used here.
 */
public interface PriorityStrategy {

	/**
	 * Composes a new queue from the chronological list of orders with respect to their prioritization.
	 *
	 * @param queue   List of all orders sorted by the their adding time (the first added order is the first element).
	 * @return prioritized list of orders from normal and premium clients, ordered for the delivery
	 * (the first element should be delivered first).
	 */
	List<QueueEntry> getPrioritizedQueue(List<QueueEntry> queue);
}
