package org.eccommerce.orderservice.exception;

public class OrderNotFound extends  RuntimeException{

    public OrderNotFound(String message) {
        super(message);
    }
}
