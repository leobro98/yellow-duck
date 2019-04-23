package com.yellowduck.lending.logic.validator;

import com.yellowduck.lending.logic.request.ClientRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ClientValidatorTest {

	private ClientValidator validator;

	@Before
	public void setUp() {
		validator = new ClientValidator();
	}

	@Test
	public void when_clientIdIsMinimal_then_noErrors() throws Exception {
		ClientRequest request = new ClientRequest(ValidatorHelper.MIN_CLIENT_ID);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(0));
	}

	@Test
	public void when_clientIdIsMaximal_then_noErrors() throws Exception {
		ClientRequest request = new ClientRequest(ValidatorHelper.MAX_CLIENT_ID);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(0));
	}

	@Test
	public void when_clientIdIsBetweenMinimalAndMaximal_then_noErrors() throws Exception {
		int clientId = (ValidatorHelper.MIN_CLIENT_ID + ValidatorHelper.MAX_CLIENT_ID) / 2;
		ClientRequest request = new ClientRequest(clientId);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(0));
	}

	@Test
	public void when_clientIdIsLessThanMinimal_then_error() throws Exception {
		ClientRequest request = new ClientRequest(ValidatorHelper.MIN_CLIENT_ID - 1);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(1));
	}

	@Test
	public void when_clientIdIsGreaterThanMaximal_then_error() throws Exception {
		ClientRequest request = new ClientRequest(ValidatorHelper.MAX_CLIENT_ID + 1);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(1));
	}
}
