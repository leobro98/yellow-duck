package com.yellowduck.lending.data;

import com.yellowduck.lending.logic.response.QueueEntry;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MapDataSourceTest {

	private static final int CLIENT_ID = 1;
	private static final int QUANTITY = 10;
	private static final int UNKNOWN_CLIENT_ID = 2;

	private MapDataSource source;

	@Before
	public void setUp() {
		source = new MapDataSource();
	}

	@Test
	public void when_addingOrder_then_orderFoundInStorage() {
		source.addOrder(CLIENT_ID, QUANTITY);

		assertTrue(source.orders.containsKey(CLIENT_ID));
		assertThat(source.orders.get(CLIENT_ID).getQuantity(), is(QUANTITY));
	}

	@Test
	public void when_cancellingOrder_then_orderRemoved() {
		source.addOrder(CLIENT_ID, QUANTITY);

		QueueEntry order = source.cancelOrder(CLIENT_ID);

		assertNotNull(order);
		assertThat(source.orders.size(), is(0));
	}

	@Test
	public void when_cancellingNotExistingOrder_then_orderNotRemoved() {
		source.addOrder(CLIENT_ID, QUANTITY);

		QueueEntry order = source.cancelOrder(UNKNOWN_CLIENT_ID);

		assertNull(order);
		assertThat(source.orders.size(), is(1));
	}

	@Test
	public void when_gettingExistingOrder_then_returnsOrder() {
		source.addOrder(CLIENT_ID, QUANTITY);

		QueueEntry order = source.getOrder(CLIENT_ID);

		assertNotNull(order);
		assertThat(order.getClientId(), is(CLIENT_ID));
		assertThat(order.getQuantity(), is(QUANTITY));
	}

	@Test
	public void when_gettingAllOrders_then_ordersSortedChronologically() throws InterruptedException {
		int clientId1 = 1001;
		int clientId2 = 1;
		int clientId3 = 2;
		int clientId4 = 1002;
		addOrder(clientId1);
		addOrder(clientId2);
		addOrder(clientId3);
		addOrder(clientId4);

		List<QueueEntry> orders = source.getAllOrders();

		assertThat(orders.get(0).getClientId(), is(clientId1));
		assertThat(orders.get(1).getClientId(), is(clientId2));
		assertThat(orders.get(2).getClientId(), is(clientId3));
		assertThat(orders.get(3).getClientId(), is(clientId4));
	}

	private void addOrder(int clientId) throws InterruptedException {
		source.orders.put(clientId, new QueueEntry(clientId, QUANTITY, LocalDateTime.now()));
		Thread.sleep(10);
	}
}
