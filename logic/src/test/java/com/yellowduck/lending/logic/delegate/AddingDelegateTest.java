package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.request.AddingRequest;
import com.yellowduck.lending.logic.response.AddingCancellingResponse;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.response.QueueEntry;
import com.yellowduck.lending.logic.validator.AddingValidator;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AddingDelegateTest {

	private static final int CLIENT_ID = 1;
	private static final int QUANTITY = 3;
	private AddingDelegate delegate;
	private QueueEntry order = null;

	@Before
	public void setUp() {
		AddingValidator validator = new AddingValidator() {
			@Override
			public List<String> validate(AddingRequest request) throws Exception {
				return new ArrayList<>();
			}
		};

		LendingDataSource dataSource = new LendingDataSource() {
			@Override
			public void addOrder(int clientId, int quantity) throws Exception {
			}

			@Override
			public QueueEntry getOrder(int clientId) {
				return order;
			}

			@Override
			public List<QueueEntry> getAllOrders() throws Exception {
				return null;
			}

			@Override
			public QueueEntry cancelOrder(int clientId) throws Exception {
				return null;
			}
		};

		delegate = new AddingDelegate(validator, dataSource);
	}

	@Test
	public void when_clientHasNoOrders_then_orderAdded() throws Exception {
		InternalResponse response = delegate.handle(new AddingRequest(CLIENT_ID, QUANTITY));

		assertNotNull(response);
		assertThat(response.getResult(), is(InternalResponse.ResultType.OK));

		assertNotNull(response.getPayload());
		AddingCancellingResponse addingResponse = (AddingCancellingResponse) response.getPayload();
		assertThat(addingResponse.getClientId(), is(CLIENT_ID));
		assertThat(addingResponse.getQuantity(), is(QUANTITY));
	}

	@Test
	public void when_clientHasOrderInQueue_then_denial() throws Exception {
		order = new QueueEntry(CLIENT_ID, QUANTITY, LocalDateTime.now());

		InternalResponse response = delegate.handle(new AddingRequest(CLIENT_ID, QUANTITY));

		assertNotNull(response);
		assertThat(response.getResult(), is(InternalResponse.ResultType.ERROR));
	}
}