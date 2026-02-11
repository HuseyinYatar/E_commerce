package org.ecommerce.paymentservice.exception;

public class InsufficientBalance extends  RuntimeException {
    public InsufficientBalance(String message) {
        super(message);
    }
}
