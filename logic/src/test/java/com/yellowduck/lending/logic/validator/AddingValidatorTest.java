package com.yellowduck.lending.logic.validator;

import com.yellowduck.lending.logic.Dispatcher;
import com.yellowduck.lending.logic.request.AddingRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AddingValidatorTest {

	private static final int CLIENT_ID = 1;

	private AddingValidator validator;

	@Before
	public void setUp() {
		validator = new AddingValidator();
	}

	@Test
	public void when_quantityIsNegative_then_error() throws Exception {
		AddingRequest request = new AddingRequest(CLIENT_ID, -1);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(1));
	}

	@Test
	public void when_quantityIsLessThanMinimal_then_error() throws Exception {
		AddingRequest request = new AddingRequest(CLIENT_ID, AddingValidator.MIN_QUANTITY - 1);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(1));
	}

	@Test
	public void when_quantityIsCorrect_then_noErrors() throws Exception {
		AddingRequest request = new AddingRequest(CLIENT_ID, AddingValidator.MIN_QUANTITY);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(0));
	}

	@Test
	public void when_quantityIsMaximal_then_noErrors() throws Exception {
		AddingRequest request = new AddingRequest(CLIENT_ID, Dispatcher.MAX_CART_LOAD);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(0));
	}

	@Test
	public void when_quantityIsGreaterThanMaximal_then_error() throws Exception {
		AddingRequest request = new AddingRequest(CLIENT_ID, Dispatcher.MAX_CART_LOAD + 1);

		List<String> errors = validator.validate(request);

		assertNotNull(errors);
		assertThat(errors.size(), is(1));
	}
}
