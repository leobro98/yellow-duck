package com.yellowduck.lending.logic;

import com.yellowduck.lending.logic.response.QueueEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * <p>There is not clear enough requirement in the assignment: "Orders from premium customers always have a higher
 * priority than ones from normal customers". How this "higher priority" should be implemented, is not clear. That is
 * why I have implemented a Strategy pattern to easily interchange the strategies.
 *
 * <p>This class implements just one of possible strategies for clients prioritization.
 * The strategy treats client orders as two queues at an airport check-in: business class and economy class.
 * Each queue advances with equal speed, however, as the business class queue is normally shorter one, a business
 * passenger can make check-in faster than economy one.
 *
 * <p>Another possible strategy could be to service first all premium customers, then all normal customers. However,
 * this have a pitfall. The service speed is limited with the ability of Joe to run with the cart - he can take a new
 * delivery once per 5 minutes. The rate, at which clients come with their orders, can be any. There can be, hence a
 * situation when a normal customer is never serviced, if premium customers come with the speed or with a higher one
 * than Joe service them.
 */
@Component
public class EqualPossibilitiesStrategy implements PriorityStrategy {

	@Override
	public List<QueueEntry> getPrioritizedQueue(List<QueueEntry> rawQueue) {
		List<QueueEntry> normalQueue = getQueueOf(QueueEntry.ClientType.NORMAL, rawQueue);
		List<QueueEntry> priorityQueue = getQueueOf(QueueEntry.ClientType.PREMIUM, rawQueue);

		return getCombinedQueue(normalQueue, priorityQueue);
	}

	private List<QueueEntry> getQueueOf(QueueEntry.ClientType clientType, List<QueueEntry> queue) {
		return queue.stream()
				.filter(x -> x.getClientType() == clientType)
				.collect(Collectors.toList());
	}

	private List<QueueEntry> getCombinedQueue(List<QueueEntry> normalQueue, List<QueueEntry> priorityQueue) {
		LinkedList<QueueEntry> first = new LinkedList<>(priorityQueue);
		LinkedList<QueueEntry> second = new LinkedList<>(normalQueue);
		List<QueueEntry> combined = new ArrayList<>();

		while (!first.isEmpty()) {
			combined.add(first.removeFirst());

			if (!second.isEmpty()) {
				combined.add(second.removeFirst());
			}
		}

		if (!second.isEmpty()) {
			combined.addAll(second);
		}

		return combined;
	}
}
