package org.assignment.wallet.exception;

public class InvalidRequestArgumentException extends RuntimeException {
    public InvalidRequestArgumentException(String message) {
        super(message);
    }
}
