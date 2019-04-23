package com.yellowduck.lending.logic;

import com.yellowduck.lending.logic.response.QueueEntry;
import java.util.List;

/**
 * API for the application's data source independent of it's implementation.
 */
public interface LendingDataSource {

	/**
	 * Adds the order to the queue.
	 *
	 * @param clientId client ID,
	 * @param quantity ordered quantity.
	 * @throws Exception if data error occurred.
	 */
	void addOrder(int clientId, int quantity) throws Exception;

	/**
	 * Returns an order of the client from the queue. Note that a client can place only one order.
	 *
	 * @param clientId client ID.
	 * @return order of the client with the given ID.
	 */
	QueueEntry getOrder(int clientId);

	/**
	 * Returns list of all orders.
	 *
	 * @return list of orders in the order of their adding (first added is the first in the list).
	 * @throws Exception if data error occurred.
	 */
	List<QueueEntry> getAllOrders() throws Exception;

	/**
	 * Cancels the order of the client. (As any client can place only one order, it's enough to identify the order.)
	 *
	 * @param clientId client ID.
	 * @return cancelled order if found; otherwise, {@literal null}.
	 * @throws Exception if data error occurred.
	 */
	QueueEntry cancelOrder(int clientId) throws Exception;
}
