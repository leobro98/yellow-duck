package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.Dispatcher;
import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.request.ClientRequest;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.response.QueueEntry;
import com.yellowduck.lending.logic.response.QueuePositionResponse;
import com.yellowduck.lending.logic.validator.ClientValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PositionDelegate extends DelegateTemplate<ClientRequest> {

	private final Dispatcher dispatcher;
	// this string should be localized in production code
	private static final String ORDER_IS_NOT_FOUND = "Your order is not found";

	@Autowired
	PositionDelegate(ClientValidator validator, LendingDataSource dataSource, Dispatcher dispatcher) {
		super(validator, dataSource);
		this.dispatcher = dispatcher;
	}

	@Override
	protected InternalResponse handle(ClientRequest request) throws Exception {
		List<QueueEntry> queue = dataSource.getAllOrders();
		QueueEntry entry = dispatcher.getPositionedEntry(request.getClientId(), queue);

		InternalResponse response;
		if (entry == null) {
			response = createErrorResponse(Collections.singletonList(ORDER_IS_NOT_FOUND));
		} else {
			response = new InternalResponse(InternalResponse.ResultType.OK,
					new QueuePositionResponse(entry.getClientId(), entry.getPosition(), entry.getWaitingTime()));
		}

		return response;
	}
}
