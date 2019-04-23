package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.request.ClientRequest;
import com.yellowduck.lending.logic.response.AddingCancellingResponse;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.response.QueueEntry;
import com.yellowduck.lending.logic.validator.ClientValidator;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CancellingDelegateTest {

	private static final int CLIENT_ID = 1;
	private static final int QUANTITY = 3;

	private CancellingDelegate delegate;
	private QueueEntry order = null;

	@Before
	public void setUp() throws Exception {
		ClientValidator validator = new ClientValidator() {
			@Override
			public List<String> validate(ClientRequest request) throws Exception {
				return new ArrayList<>();
			}
		};

		LendingDataSource dataSource= new LendingDataSource() {
			@Override
			public void addOrder(int clientId, int quantity) { }

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
				return order;
			}
		};

		delegate = new CancellingDelegate(validator, dataSource);
	}

	@Test
	public void when_orderIsInQueue_then_returnsCancelledOrder() throws Exception {
		order = new QueueEntry(CLIENT_ID, QUANTITY, LocalDateTime.now());

		InternalResponse response = delegate.handle(new ClientRequest(CLIENT_ID));

		assertNotNull(response);
		assertThat(response.getResult(), is(InternalResponse.ResultType.OK));

		assertNotNull(response.getPayload());
		AddingCancellingResponse cancellingResponse = (AddingCancellingResponse) response.getPayload();
		assertThat(cancellingResponse.getClientId(), is(CLIENT_ID));
		assertThat(cancellingResponse.getQuantity(), is(QUANTITY));
	}

	@Test
	public void when_orderIsMissing_then_returnsError() throws Exception {
		InternalResponse response = delegate.handle(new ClientRequest(CLIENT_ID));

		assertNotNull(response);
		assertThat(response.getResult(), is(InternalResponse.ResultType.ERROR));
	}
}
