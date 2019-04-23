package com.yellowduck.lending.rest;

import com.yellowduck.lending.logic.delegate.AddingDelegate;
import com.yellowduck.lending.logic.delegate.CancellingDelegate;
import com.yellowduck.lending.logic.delegate.DeliveryDelegate;
import com.yellowduck.lending.logic.delegate.PositionDelegate;
import com.yellowduck.lending.logic.delegate.QueueDelegate;
import com.yellowduck.lending.logic.request.ClientRequest;
import com.yellowduck.lending.logic.request.EmptyRequest;
import com.yellowduck.lending.logic.response.InternalResponse;
import com.yellowduck.lending.logic.request.AddingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/lending")
public class LendingController {

	private ResponseFactory responseFactory;
	private AddingDelegate orderAdder;
	private PositionDelegate positionInformer;
	private QueueDelegate queueInformer;
	private DeliveryDelegate deliverer;
	private CancellingDelegate orderCanceller;

	@Autowired
	public LendingController(ResponseFactory responseFactory, AddingDelegate orderAdder,
			PositionDelegate positionInformer, QueueDelegate queueInformer, DeliveryDelegate deliverer,
			CancellingDelegate orderCanceller) {

		this.responseFactory = responseFactory;
		this.orderAdder = orderAdder;
		this.positionInformer = positionInformer;
		this.queueInformer = queueInformer;
		this.deliverer = deliverer;
		this.orderCanceller = orderCanceller;
	}

	/**
	 * Adds items to the queue. The capacity of the Joe's cart limits the quantity to 25 per one order.
	 * You could test it using e.g. Curl command line tool or Advanced REST Client.
	 *
	 * @param clientId client ID,
	 * @param quantity ordered quantity of items.
	 * @return confirmation or error messages if something went wrong.
	 */
	@PostMapping("/add-order")
	public ResponseEntity<?> addOrder(@RequestParam(name = "id") int clientId, @RequestParam(name = "q") int quantity) {
		InternalResponse response = orderAdder.process(new AddingRequest(clientId, quantity));
		return responseFactory.createResponse(response);
	}

	/**
	 * Returns the queue position and approximate wait time for the client.
	 *
	 * @param clientId client ID.
	 * @return response containing client's queue position and approximate wait time or error messages.
	 */
	@GetMapping("/my-position")
	public ResponseEntity<?> getMyPosition(@RequestParam(name = "id") int clientId) {
		InternalResponse response = positionInformer.process(new ClientRequest(clientId));
		return responseFactory.createResponse(response);
	}

	/**
	 * Returns all entries in the queue with the approximate wait time.
	 *
	 * @return all entries in the queue containing client ID, quantity and approximate wait time.
	 */
	@GetMapping("/queue")
	public ResponseEntity<?> getQueue() {
		InternalResponse response = queueInformer.process(new EmptyRequest());
		return responseFactory.createResponse(response);
	}

	/**
	 * Composes next delivery which should be placed in the cart. This request does not affect the queue. In order
	 * to reflect retrieving the orders from the queue into the cart, Joe (or web service client) needs to call
	 * cancelOrder several times with appropriate client IDs.
	 *
	 * @return orders that should be included into the next delivery.
	 */
	@GetMapping("/next-delivery")
	public ResponseEntity<?> getDelivery() {
		InternalResponse response = deliverer.process(new EmptyRequest());
		return responseFactory.createResponse(response);
	}

	/**
	 * Cancels the client's order.
	 * You could test it using e.g. Curl command line tool or Advanced REST Client.
	 *
	 * @param clientId client ID (it's enough as a client can only place one order).
	 * @return confirmation or error messages if something went wrong.
	 */
	@DeleteMapping("/cancel-order")
	public ResponseEntity<?> cancelOrder(@RequestParam(name = "id") int clientId) {
		InternalResponse response = orderCanceller.process(new ClientRequest(clientId));
		return responseFactory.createResponse(response);
	}
}
