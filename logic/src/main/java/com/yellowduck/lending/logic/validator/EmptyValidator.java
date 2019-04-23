package com.yellowduck.lending.logic.validator;

import com.yellowduck.lending.logic.request.EmptyRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EmptyValidator implements RequestValidator<EmptyRequest> {

	@Override
	public List<String> validate(EmptyRequest request) throws Exception {
		return new ArrayList<>();
	}
}
