package com.yellowduck.lending.logic;

import com.yellowduck.lending.logic.response.QueueEntry;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.OrderComparator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.yellowduck.lending.logic.Dispatcher.MAX_PREMIUM_CLIENT_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;

public class EqualPossibilitiesStrategyTest {

	private static final int ID_PREMIUM_1 = 1;
	private static final int ID_PREMIUM_2 = 2;
	private static final int ID_PREMIUM_3 = 3;
	private static final int ID_NORMAL_1 = 1001;
	private static final int ID_NORMAL_2 = 1002;
	private static final int ID_NORMAL_3 = 1003;

	private PriorityStrategy strategy;

	@Before
	public void setUp() throws Exception {
		strategy = new EqualPossibilitiesStrategy();
	}

	@Test
	public void when_severalOrdersPassedAndPriorityShorter_then_sortsOrdersByPriority() {
		List<QueueEntry> rawQueue = getTestQueuePriorityShorter();

		List<QueueEntry> queue = strategy.getPrioritizedQueue(rawQueue);

		assertNotNull(queue);
		assertThat(queue.size(), is(5));
		assertThat(queue.get(0).getClientId(), is(ID_PREMIUM_1));
		assertThat(queue.get(1).getClientId(), is(ID_NORMAL_1));
		assertThat(queue.get(2).getClientId(), is(ID_PREMIUM_2));
		assertThat(queue.get(3).getClientId(), is(ID_NORMAL_2));
		assertThat(queue.get(4).getClientId(), is(ID_NORMAL_3));
	}

	@Test
	public void when_severalOrdersPassedAndPriorityLonger_then_sortsOrdersByPriority() {
		List<QueueEntry> rawQueue = getTestQueuePriorityLonger();

		List<QueueEntry> queue = strategy.getPrioritizedQueue(rawQueue);

		assertNotNull(queue);
		assertThat(queue.size(), is(5));
		assertThat(queue.get(0).getClientId(), is(ID_PREMIUM_1));
		assertThat(queue.get(1).getClientId(), is(ID_NORMAL_1));
		assertThat(queue.get(2).getClientId(), is(ID_PREMIUM_2));
		assertThat(queue.get(3).getClientId(), is(ID_NORMAL_2));
		assertThat(queue.get(4).getClientId(), is(ID_PREMIUM_3));
	}

	/**
	 * This should work for every strategy.
	 */
	@Test
	public void when_normalQueuePassed_then_ordersGoInSubmissionOrder() {
		List<QueueEntry> rawQueue = getTestQueueOf3NormalClients();

		List<QueueEntry> queue = strategy.getPrioritizedQueue(rawQueue);

		assertThatOrdersGoInSubmissionOrder(queue);
	}

	/**
	 * This should work for every strategy.
	 */
	@Test
	public void when_priorityQueuePassed_then_ordersGoInSubmissionOrder() {
		List<QueueEntry> rawQueue = getTestQueueOf3PriorityClients();

		List<QueueEntry> queue = strategy.getPrioritizedQueue(rawQueue);

		assertThatOrdersGoInSubmissionOrder(queue);
	}

	/**
	 * This should work for every strategy.
	 */
	@Test
	public void when_mixedQueuePassed_then_ordersGoInSubmissionOrder() {
		List<QueueEntry> rawQueue = getTestQueueOf3NormalClients();
		rawQueue.addAll(getTestQueueOf3PriorityClients());

		List<QueueEntry> queue = strategy.getPrioritizedQueue(rawQueue);

		LocalDateTime normalTime = null;
		LocalDateTime priorityTime = null;

		for (QueueEntry order : queue) {
			if (order.getClientType() == QueueEntry.ClientType.NORMAL) {
				if (normalTime == null) {
					// first order from normal client, initialize comparison base
					normalTime = order.getSubmissionTime();
				}

				assertThat(order.getSubmissionTime(), greaterThanOrEqualTo(normalTime));
			} else {
				if (priorityTime == null) {
					// first order from priority client, initialize comparison base
					priorityTime = order.getSubmissionTime();
				}

				assertThat(order.getSubmissionTime(), greaterThanOrEqualTo(priorityTime));
			}
		}
	}

	private void assertThatOrdersGoInSubmissionOrder(List<QueueEntry> queue) {
		assertNotNull(queue);
		assertThat(queue.size(), is(3));

		LocalDateTime time = queue.get(0).getSubmissionTime();

		for (QueueEntry order : queue) {
			assertThat(order.getSubmissionTime(), greaterThanOrEqualTo(time));
			time = order.getSubmissionTime();
		}
	}

	private List<QueueEntry> getTestQueuePriorityShorter() {
		List<QueueEntry> queue = getTestQueueOf3NormalClients();
		queue.addAll(getTestQueueOf2PriorityClients());
		return queue;
	}

	private List<QueueEntry> getTestQueuePriorityLonger() {
		List<QueueEntry> queue = getTestQueueOf2NormalClients();
		queue.addAll(getTestQueueOf3PriorityClients());
		return queue;
	}

	private List<QueueEntry> getTestQueueOf2NormalClients() {
		List<QueueEntry> entries = new ArrayList<>();
		entries.add(getQueueEntry(ID_NORMAL_1, 5, LocalDateTime.now()));
		entries.add(getQueueEntry(ID_NORMAL_2, 15, LocalDateTime.now()));
		return entries;
	}

	private List<QueueEntry> getTestQueueOf3NormalClients() {
		List<QueueEntry> entries = new ArrayList<>(getTestQueueOf2NormalClients());
		entries.add(getQueueEntry(ID_NORMAL_3, 25, LocalDateTime.now()));
		return entries;
	}

	private List<QueueEntry> getTestQueueOf2PriorityClients() {
		List<QueueEntry> entries = new ArrayList<>();
		entries.add(getQueueEntry(ID_PREMIUM_1, 5, LocalDateTime.now()));
		entries.add(getQueueEntry(ID_PREMIUM_2, 15, LocalDateTime.now()));
		return entries;
	}

	private List<QueueEntry> getTestQueueOf3PriorityClients() {
		List<QueueEntry> entries = new ArrayList<>(getTestQueueOf2PriorityClients());
		entries.add(getQueueEntry(ID_PREMIUM_3, 10, LocalDateTime.now()));
		return entries;
	}

	private QueueEntry getQueueEntry(int id, int quantity, LocalDateTime submissionTime) {
		QueueEntry entry = new QueueEntry(id, quantity, submissionTime);

		if (entry.getClientId() < MAX_PREMIUM_CLIENT_ID) {
			entry.setClientType(QueueEntry.ClientType.PREMIUM);
		} else {
			entry.setClientType(QueueEntry.ClientType.NORMAL);
		}
		return entry;
	}
}
