package com.yellowduck.lending.rest;

import com.yellowduck.lending.logic.delegate.*;
import com.yellowduck.lending.logic.request.AddingRequest;
import com.yellowduck.lending.logic.request.ClientRequest;
import com.yellowduck.lending.logic.request.EmptyRequest;
import com.yellowduck.lending.logic.response.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LendingControllerTest {

	private static final String ADD_ORDER_URL = "/lending/add-order";
	private static final String MY_POSITION_URL = "/lending/my-position";
	private static final String QUEUE_URL = "/lending/queue";
	private static final String NEXT_DELIVERY_URL = "/lending/next-delivery";
	private static final String CANCEL_ORDER_URL = "/lending/cancel-order";

	private static final String CLIENT_ID_PARAMETER = "id";
	private static final String QUANTITY_PARAMETER = "q";
	private static final String ID_PARAM_VALUE = "2";
	private static final String Q_PARAM_VALUE = "3";

	private static final int CLIENT_ID = 1;
	private static final int QUANTITY = 5;
	private static final int POSITION = 4;
	private static final Duration WAITING_TIME_0 = Duration.ofMinutes(0);
	private static final String WAITING_TIME_0_STRING = "PT0S";
	private static final String CLIENT_TYPE = "PREMIUM";

	private static final String MESSAGE_OK = "OK";
	private static final String MESSAGE_ERROR = "ERROR";

	@MockBean
	private AddingDelegate addingDelegate;

	@MockBean
	private PositionDelegate positionDelegate;

	@MockBean
	private QueueDelegate queueDelegate;

	@MockBean
	private DeliveryDelegate deliveryDelegate;

	@MockBean
	private CancellingDelegate cancellingDelegate;

	@Autowired
	private MockMvc mvc;

	private static LocalDateTime submissionTime;
	private static String timeString;

	@BeforeClass
	public static void setUpBeforeClass() {
		// when time ends with 0, Spring truncates this 0 during marshalling to JSON, which causes failing tests
		do {
			submissionTime = LocalDateTime.now();
			timeString = submissionTime.toString();
		} while (timeString.endsWith("0"));
	}

	private QueueEntry createQueueEntry() {
		QueueEntry entry = new QueueEntry(CLIENT_ID, QUANTITY, submissionTime);
		entry.setClientType(QueueEntry.ClientType.PREMIUM);
		entry.setPosition(POSITION);
		entry.setWaitingTime(WAITING_TIME_0);
		return entry;
	}

	/**
	 * All requests are handled exactly the same, so this test can be done for a one request only.
	 */
	@Test
	public void when_requestValidationFails_then_statusBadRequest() throws Exception {
		given(addingDelegate.process(any(AddingRequest.class)))
				.willReturn(new InternalResponse(
						InternalResponse.ResultType.ERROR,
						new AddingCancellingResponse(CLIENT_ID, QUANTITY, MESSAGE_ERROR)));

		mvc.perform(
				MockMvcRequestBuilders.post(ADD_ORDER_URL)
						.param(CLIENT_ID_PARAMETER, ID_PARAM_VALUE)
						.param(QUANTITY_PARAMETER, Q_PARAM_VALUE)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	/**
	 * All requests are handled exactly the same, so this test can be done for a one request only.
	 */
	@Test
	public void when_requestProcessingFails_then_statusServerError() throws Exception {
		given(addingDelegate.process(any(AddingRequest.class)))
				.willReturn(new InternalResponse(
						InternalResponse.ResultType.FATAL,
						new AddingCancellingResponse(CLIENT_ID, QUANTITY, MESSAGE_ERROR)));

		mvc.perform(
				MockMvcRequestBuilders.post(ADD_ORDER_URL)
						.param(CLIENT_ID_PARAMETER, ID_PARAM_VALUE)
						.param(QUANTITY_PARAMETER, Q_PARAM_VALUE)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError());
	}

	@Test
	public void when_addOrder_then_statusOkAndValuesFromService() throws Exception {
		given(addingDelegate.process(any(AddingRequest.class)))
				.willReturn(new InternalResponse(
						InternalResponse.ResultType.OK,
						new AddingCancellingResponse(CLIENT_ID, QUANTITY, MESSAGE_OK)));

		mvc.perform(
				MockMvcRequestBuilders.post(ADD_ORDER_URL)
						.param(CLIENT_ID_PARAMETER, ID_PARAM_VALUE)
						.param(QUANTITY_PARAMETER, Q_PARAM_VALUE)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.payload.clientId", is(CLIENT_ID)))
				.andExpect(jsonPath("$.payload.quantity", is(QUANTITY)))
				.andExpect(jsonPath("$.payload.message", is(MESSAGE_OK)));
	}

	@Test
	public void when_getMyPosition_then_statusOkAndValuesFromService() throws Exception {
		given(positionDelegate.process(any(ClientRequest.class)))
				.willReturn(new InternalResponse(
						InternalResponse.ResultType.OK,
						new QueuePositionResponse(CLIENT_ID, POSITION, WAITING_TIME_0)));

		mvc.perform(
				MockMvcRequestBuilders.get(MY_POSITION_URL)
						.param(CLIENT_ID_PARAMETER, ID_PARAM_VALUE)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.payload.clientId", is(CLIENT_ID)))
				.andExpect(jsonPath("$.payload.position", is(POSITION)))
				.andExpect(jsonPath("$.payload.waitingTime", is(WAITING_TIME_0_STRING)));
	}

	@Test
	public void when_getQueue_then_statusOkAndValuesFromService() throws Exception {
		QueueEntry entry = createQueueEntry();
		given(queueDelegate.process(any(EmptyRequest.class)))
				.willReturn(new InternalResponse(
						InternalResponse.ResultType.OK,
						new QueueResponse(Collections.singletonList(entry))));

		mvc.perform(
				MockMvcRequestBuilders.get(QUEUE_URL)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.payload.entries[0].clientId", is(CLIENT_ID)))
				.andExpect(jsonPath("$.payload.entries[0].quantity", is(QUANTITY)))
				.andExpect(jsonPath("$.payload.entries[0].submissionTime", is(timeString)))
				.andExpect(jsonPath("$.payload.entries[0].clientType", is(CLIENT_TYPE)))
				.andExpect(jsonPath("$.payload.entries[0].position", is(POSITION)))
				.andExpect(jsonPath("$.payload.entries[0].waitingTime", is(WAITING_TIME_0_STRING)));
	}

	@Test
	public void when_getDelivery_then_statusOkAndValuesFromService() throws Exception {
		QueueEntry entry = createQueueEntry();
		given(deliveryDelegate.process(any(EmptyRequest.class)))
				.willReturn(new InternalResponse(
						InternalResponse.ResultType.OK,
						new QueueResponse(Collections.singletonList(entry))));

		mvc.perform(
				MockMvcRequestBuilders.get(NEXT_DELIVERY_URL)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.payload.entries[0].clientId", is(CLIENT_ID)))
				.andExpect(jsonPath("$.payload.entries[0].quantity", is(QUANTITY)))
				.andExpect(jsonPath("$.payload.entries[0].submissionTime", is(timeString)))
				.andExpect(jsonPath("$.payload.entries[0].clientType", is(CLIENT_TYPE)))
				.andExpect(jsonPath("$.payload.entries[0].position", is(POSITION)))
				.andExpect(jsonPath("$.payload.entries[0].waitingTime", is(WAITING_TIME_0_STRING)));
	}

	@Test
	public void when_cancelOrder_then_statusOkAndValuesFromService() throws Exception {
		given(cancellingDelegate.process(any(ClientRequest.class)))
				.willReturn(new InternalResponse(
						InternalResponse.ResultType.OK,
						new AddingCancellingResponse(CLIENT_ID, QUANTITY, MESSAGE_OK)));

		mvc.perform(
				MockMvcRequestBuilders.delete(CANCEL_ORDER_URL)
						.param(CLIENT_ID_PARAMETER, ID_PARAM_VALUE)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.payload.clientId", is(CLIENT_ID)))
				.andExpect(jsonPath("$.payload.quantity", is(QUANTITY)))
				.andExpect(jsonPath("$.payload.message", is(MESSAGE_OK)));
	}
}
