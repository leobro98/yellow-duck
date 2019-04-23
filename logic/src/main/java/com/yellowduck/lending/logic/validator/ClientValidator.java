package com.yellowduck.lending.logic.validator;

import com.yellowduck.lending.logic.request.ClientRequest;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ClientValidator implements RequestValidator<ClientRequest> {

	@Override
	public List<String> validate(ClientRequest request) throws Exception {
		return ValidatorHelper.validateClientId(request);
	}
}
