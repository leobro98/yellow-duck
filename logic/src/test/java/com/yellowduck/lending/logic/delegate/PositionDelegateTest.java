package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.Dispatcher;
import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.PriorityStrategy;
import com.yellowduck.lending.logic.request.ClientRequest;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.response.QueueEntry;
import com.yellowduck.lending.logic.response.QueuePositionResponse;
import com.yellowduck.lending.logic.validator.ClientValidator;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PositionDelegateTest {

	private static final int CLIENT_ID = 1;
	private static final int QUANTITY = 3;

	private PositionDelegate delegate;
	private QueueEntry order = null;

	@Before
	public void setUp() {
		ClientValidator validator = new ClientValidator() {
			@Override
			public List<String> validate(ClientRequest request) {
				return new ArrayList<>();
			}
		};

		LendingDataSource dataSource = new LendingDataSource() {
			@Override
			public void addOrder(int clientId, int quantity) {
			}

			@Override
			public QueueEntry getOrder(int clientId) {
				return null;
			}

			@Override
			public List<QueueEntry> getAllOrders() {
				return null;
			}

			@Override
			public QueueEntry cancelOrder(int clientId) {
				return null;
			}
		};

		PriorityStrategy strategy = queue -> null;
		Dispatcher dispatcher = new Dispatcher(strategy) {
			@Override
			public QueueEntry getPositionedEntry(int clientId, List<QueueEntry> rawQueue) {
				return order;
			}
		};

		delegate = new PositionDelegate(validator, dataSource, dispatcher);
	}

	@Test
	public void when_orderIsFound_then_returnsPositionAndWaitingTime() throws Exception {
		order = new QueueEntry(CLIENT_ID, QUANTITY, LocalDateTime.now());
		int position = 5;
		Duration waitingTime = Duration.ofMinutes(15);
		order.setPosition(position);
		order.setWaitingTime(waitingTime);

		InternalResponse response = delegate.handle(new ClientRequest(CLIENT_ID));

		assertNotNull(response);
		assertThat(response.getResult(), is(InternalResponse.ResultType.OK));

		assertNotNull(response.getPayload());
		QueuePositionResponse positionResponse = (QueuePositionResponse) response.getPayload();
		assertThat(positionResponse.getClientId(), is(CLIENT_ID));
		assertThat(positionResponse.getPosition(), is(position));
		assertThat(positionResponse.getWaitingTime(), is(waitingTime));
	}

	@Test
	public void when_orderNotFound_then_returnsError() throws Exception {
		InternalResponse response = delegate.handle(new ClientRequest(CLIENT_ID));

		assertNotNull(response);
		assertThat(response.getResult(), is(InternalResponse.ResultType.ERROR));
	}
}
