package com.yellowduck.lending.logic.validator;

import com.yellowduck.lending.logic.request.ClientRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains common validation methods.
 */
class ValidatorHelper {

	static final int MIN_CLIENT_ID = 1;
	static final int MAX_CLIENT_ID = 20000;

	// this string should be localized in the production code
	private static final String CLIENT_ID_ERROR = "Client ID may be from %d to %d";

	static List<String> validateClientId(ClientRequest request) {
		List<String> errors = new ArrayList<>();
		int clientId = request.getClientId();

		if (clientId < MIN_CLIENT_ID || clientId > MAX_CLIENT_ID) {
			errors.add(String.format(CLIENT_ID_ERROR, MIN_CLIENT_ID, MAX_CLIENT_ID));
		}
		return errors;
	}
}
