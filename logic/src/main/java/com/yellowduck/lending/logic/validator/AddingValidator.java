package com.yellowduck.lending.logic.validator;

import com.yellowduck.lending.logic.Dispatcher;
import com.yellowduck.lending.logic.request.AddingRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AddingValidator implements RequestValidator<AddingRequest> {

	static final int MIN_QUANTITY = 1;

	private static final String QUANTITY_ERROR = "Ordered quantity may be from %d to %d";

	@Override
	public List<String> validate(AddingRequest request) throws Exception {
		List<String> errors = new ArrayList<>();
		errors.addAll(ValidatorHelper.validateClientId(request));
		errors.addAll(validateQuantity(request));
		return errors;
	}

	/**
	 * Joe's cart is able to carry 25 rubber ducks and skipping, changing or splitting any orders is not allowed,
	 * hence the maximum ordered quantity is 25.
	 */
	private static List<String> validateQuantity(AddingRequest request) {
		List<String> errors = new ArrayList<>();
		int quantity = request.getQuantity();

		if (quantity < MIN_QUANTITY || quantity > Dispatcher.MAX_CART_LOAD) {
			errors.add(String.format(QUANTITY_ERROR, MIN_QUANTITY, Dispatcher.MAX_CART_LOAD));
		}
		return errors;
	}
}
