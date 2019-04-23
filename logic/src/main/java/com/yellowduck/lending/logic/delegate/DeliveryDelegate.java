package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.Dispatcher;
import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.request.EmptyRequest;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.response.QueueEntry;
import com.yellowduck.lending.logic.response.QueueResponse;
import com.yellowduck.lending.logic.validator.EmptyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryDelegate extends DelegateTemplate<EmptyRequest> {

	private final Dispatcher dispatcher;

	@Autowired
	protected DeliveryDelegate(EmptyValidator validator, LendingDataSource dataSource, Dispatcher dispatcher) {
		super(validator, dataSource);
		this.dispatcher = dispatcher;
	}

	@Override
	protected InternalResponse handle(EmptyRequest request) throws Exception {
		List<QueueEntry> queue = dataSource.getAllOrders();
		List<QueueEntry> delivery = dispatcher.getNextDelivery(queue);

		return new InternalResponse(InternalResponse.ResultType.OK, new QueueResponse(delivery));
	}
}
