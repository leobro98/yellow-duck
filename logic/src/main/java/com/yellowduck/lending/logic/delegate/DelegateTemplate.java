package com.yellowduck.lending.logic.delegate;

import com.yellowduck.lending.logic.LendingDataSource;
import com.yellowduck.lending.logic.request.InternalRequest;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.validator.RequestValidator;

import java.util.List;

/**
 * Base class for all delegates, contains common functionality.
 *
 * @param <RequestT> type of the request, which the inheritor delegate is servicing.
 */
public abstract class DelegateTemplate<RequestT extends InternalRequest> {

	private static final String TECHNICAL_ERROR = "Technical error occurred";

	private RequestValidator<RequestT> validator;
	LendingDataSource dataSource;

	/**
	 * Sets the required dependencies.
	 *
	 * @param validator  validator specific for certain delegate,
	 * @param dataSource application's data source.
	 */
	DelegateTemplate(RequestValidator<RequestT> validator, LendingDataSource dataSource) {
		this.validator = validator;
		this.dataSource = dataSource;
	}

	/**
	 * The only public method of the delegate, which is called to process the request.
	 *
	 * @param request the request for processing.
	 * @return response object with the list of possible errors and alternative payload object, different for every delegate.
	 */
	public InternalResponse process(RequestT request) {
		try {
			List<String> errors = validator.validate(request);

			if (!errors.isEmpty()) {
				return createErrorResponse(errors);
			}

			return handle(request);
		} catch (Exception e) {
			return createTechnicalErrorResponse(e);
		}
	}

	InternalResponse createErrorResponse(List<String> errors) {
		return new InternalResponse(InternalResponse.ResultType.ERROR, errors);
	}

	/**
	 * Logging needs to be added here.
	 */
	private InternalResponse createTechnicalErrorResponse(Exception e) {
		System.out.println(e);
		return new InternalResponse(InternalResponse.ResultType.FATAL, TECHNICAL_ERROR);
	}

	/**
	 * Contains functionality specific for every delegate that overrides it.
	 *
	 * @param request the request for processing.
	 * @return response object with the list of possible errors and alternative payload object, different for every delegate.
	 * @throws Exception any unexpected exception.
	 */
	protected abstract InternalResponse handle(RequestT request) throws Exception;
}
