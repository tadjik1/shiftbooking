package com.sergey.zelenov.shiftbooking.exception;

public class InvalidJWTSignature extends RuntimeException {
    public InvalidJWTSignature(String message) {
        super(message);
    }
}
