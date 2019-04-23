package com.yellowduck.lending.logic.validator;

import com.yellowduck.lending.logic.request.InternalRequest;
import java.util.List;

/**
 * Interface that should be implemented by every request validator.
 *
 * @param <RequestT> type of the specific request to be validated.
 */
public interface RequestValidator<RequestT extends InternalRequest> {

	/**
	 * Validates the information passed in the request.
	 *
	 * @param request the request to be validated.
	 * @return list of errors found in the request data.
	 * @throws Exception if any unexpected exception occurs.
	 */
	List<String> validate(RequestT request) throws Exception;
}
