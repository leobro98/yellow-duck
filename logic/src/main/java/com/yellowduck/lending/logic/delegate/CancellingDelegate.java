package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.request.ClientRequest;
import com.yellowduck.lending.logic.response.AddingCancellingResponse;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.response.QueueEntry;
import com.yellowduck.lending.logic.validator.ClientValidator;
import org.springframework.stereotype.Service;

@Service
public class CancellingDelegate extends DelegateTemplate<ClientRequest> {

	// these strings should be localized in production code
	private static final String CONFIRMATION = "Your order is cancelled";
	private static final String DENIAL = "You don't have an order in the queue";

	protected CancellingDelegate(ClientValidator validator, LendingDataSource dataSource) {
		super(validator, dataSource);
	}

	@Override
	protected InternalResponse handle(ClientRequest request) throws Exception {
		int clientId = request.getClientId();
		QueueEntry cancelledOrder = dataSource.cancelOrder(clientId);
		AddingCancellingResponse response;

		if (cancelledOrder == null) {
			response = new AddingCancellingResponse(clientId, 0, DENIAL);
			return new InternalResponse(InternalResponse.ResultType.ERROR, response);
		}

		response = new AddingCancellingResponse(clientId, cancelledOrder.getQuantity(), CONFIRMATION);
		return new InternalResponse(InternalResponse.ResultType.OK, response);
	}
}
