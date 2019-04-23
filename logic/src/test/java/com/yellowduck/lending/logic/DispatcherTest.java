package com.yellowduck.lending.logic;

import com.yellowduck.lending.logic.response.QueueEntry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static com.yellowduck.lending.logic.Dispatcher.MAX_CART_LOAD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DispatcherTest {

	private Dispatcher dispatcher;

	private static final int ID_2 = 1001;
	private static final int ID_4 = 1002;
	private static final int ID_1 = 1;
	private static final int ID_3 = 2;

	private static final int QUANTITY_1 = 3;
	private static final int QUANTITY_2 = 5;
	private static final int QUANTITY_3 = 15;
	private static final int QUANTITY_4 = 11;

	@org.junit.Before
	public void setUp() {
		dispatcher = new Dispatcher(new EqualPossibilitiesStrategy());
	}

	@Test
	public void when_getPrioritizedQueue_then_ordersHavePositionAndWaitingTime() {
		List<QueueEntry> rawQueue = getTestQueue();

		List<QueueEntry> queue = dispatcher.getPrioritizedQueue(rawQueue);

		assertNotNull(queue);
		assertThat(queue.size(), is(4));

		assertThat(queue.get(0).getPosition(), is(1));
		assertThat(queue.get(1).getPosition(), is(2));
		assertThat(queue.get(2).getPosition(), is(3));
		assertThat(queue.get(3).getPosition(), is(4));

		assertNotNull(queue.get(0).getWaitingTime());
		assertNotNull(queue.get(1).getWaitingTime());
		assertNotNull(queue.get(2).getWaitingTime());
		assertNotNull(queue.get(3).getWaitingTime());
	}

	@Test
	public void when_severalOrdersPassed_then_selectsDelivery() {
		List<QueueEntry> rawQueue = getTestQueue();

		List<QueueEntry> delivery = dispatcher.getNextDelivery(rawQueue);

		assertNotNull(delivery);
		assertThat(delivery.size(), is(QUANTITY_1));

		assertThat(delivery.get(0).getQuantity(), is(QUANTITY_1));
		assertThat(delivery.get(1).getQuantity(), is(QUANTITY_2));
		assertThat(delivery.get(2).getQuantity(), is(QUANTITY_3));

		assertTrue(delivery.get(0).getQuantity()
				+ delivery.get(1).getQuantity()
				+ delivery.get(2).getQuantity()
				<= MAX_CART_LOAD);
	}

	@Test
	public void when_severalOrdersPassed_then_queuePositionAndTimeIsCorrect() {
		List<QueueEntry> rawQueue = getTestQueue();

		QueueEntry entry = dispatcher.getPositionedEntry(ID_4, rawQueue);

		assertNotNull(entry);
		assertThat(entry.getPosition(), is(4));
		assertThat(entry.getWaitingTime(), is(Duration.ofMinutes(5)));
	}

	private List<QueueEntry> getTestQueue() {
		List<QueueEntry> queue = new ArrayList<>();
		queue.add(new QueueEntry(ID_1, QUANTITY_1, LocalDateTime.now()));
		queue.add(new QueueEntry(ID_2, QUANTITY_2, LocalDateTime.now()));
		queue.add(new QueueEntry(ID_3, QUANTITY_3, LocalDateTime.now()));
		queue.add(new QueueEntry(ID_4, QUANTITY_4, LocalDateTime.now()));
		return queue;
	}
}
