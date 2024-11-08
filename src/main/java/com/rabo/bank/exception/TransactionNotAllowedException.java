package com.rabo.bank.exception;

public class TransactionNotAllowedException extends RuntimeException {

    public TransactionNotAllowedException(String message) {
        super(message);
    }
}
