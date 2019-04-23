package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.request.AddingRequest;
import com.yellowduck.lending.logic.response.AddingCancellingResponse;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.response.QueueEntry;
import com.yellowduck.lending.logic.validator.AddingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddingDelegate extends DelegateTemplate<AddingRequest> {

	// these strings should be localized in production code
	private static final String CONFIRMATION = "Your order is placed";
	private static final String DENIAL = "You have already an order in the queue, this wasn't added";

	@Autowired
	public AddingDelegate(AddingValidator validator, LendingDataSource dataSource) {
		super(validator, dataSource);
	}

	@Override
	protected InternalResponse handle(AddingRequest request) throws Exception {
		int id = request.getClientId();
		int quantity = request.getQuantity();
		AddingCancellingResponse response = new AddingCancellingResponse(id, quantity);

		if (isInQueue(id)) {
			response.setMessage(DENIAL);
			return new InternalResponse(InternalResponse.ResultType.ERROR, response);
		}

		dataSource.addOrder(id, quantity);

		response.setMessage(CONFIRMATION);
		return new InternalResponse(InternalResponse.ResultType.OK, response);
	}

	private boolean isInQueue(int clientId) {
		QueueEntry order = dataSource.getOrder(clientId);
		return order != null;
	}
}
