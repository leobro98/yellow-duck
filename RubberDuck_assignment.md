# Rubber Duck Lending

Joe works in a storehouse of a rubber duck lender. He is responsible for bringing rubber ducks to the front desk and for returning them to the store as well. But the manager is not satisfied with service the storehouse is providing to the front desk because either it takes too long before a delivery arrives or only a few items arrive. Joe's manager wants to fix this and asks Joe to write a web service to accept the orders and provide a list of items to deliver to the front desk. Desperate Joe remembers his friends who is working in a software company as a software engineer... and your phone rings. Can you help Joe?

## Problem Statement

Joe explains to you that he got a bunch of requirements for the priority queue and the web service. The constraints he was given are:

* The service should implement a REST-ful service.
* All orders will be placed in a single queue.
* Each order is comprised by the ID of the client and the requested quantity.
* A client can only place one order and existing orders cannot be modified.
* Client IDs are in the range of 1 to 20000 and customers with IDs less than 1000 are premium customers.
* Orders are sorted by the number of seconds they are in the queue.
* Orders from premium customers always have a higher priority than normal customers.

Joe is supposed to look at the queue every 5 minutes and bring as many orders to the front desk as possible. His cart is able to carry 25 rubber ducks and he should put as many orders into his cart without skipping, changing or splitting any orders.

This leads to the following list of requirements for the endpoints:

* An endpoint for adding items to the queue. This endpoint must take two parameters, the ID of the client and the quantity.
* An endpoint for the client where he can check his queue position and approximate wait time. Counting starts at 1.
* An endpoint which allows his manager to see all entries in the queue with the approximate wait time.
* An endpoint to retrieve his next delivery which should be placed in the cart.
* An endpoint to cancel an order. This endpoint should accept only the client ID.

Endpoints should follow REST best practices, parameters should be passed in the fashion that is most closely matched by REST principles.

## Task

Please provide a Java program which accomplishes the above along with a build script.
