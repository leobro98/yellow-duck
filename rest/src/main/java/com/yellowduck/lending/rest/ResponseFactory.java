package com.yellowduck.lending.rest;

import com.yellowduck.lending.logic.response.InternalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
class ResponseFactory {

	ResponseEntity<?> createResponse(InternalResponse internalResponse) {
		CommonResponse response = new CommonResponse(internalResponse.getPayload());
		transferMessages(internalResponse, response);

		if (isOK(internalResponse)) {
			return ResponseEntity.ok()
					.body(response);
		} else if (isError(internalResponse)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(response);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(response);
	}

	private void transferMessages(InternalResponse internalResponse, CommonResponse response) {
		for (String msg : internalResponse.getMessages()) {
			response.addMessage(msg);
		}
	}

	private boolean isOK(InternalResponse internalResponse) {
		return internalResponse.getResult() == InternalResponse.ResultType.OK;
	}

	private boolean isError(InternalResponse internalResponse) {
		return internalResponse.getResult() == InternalResponse.ResultType.ERROR;
	}
}
