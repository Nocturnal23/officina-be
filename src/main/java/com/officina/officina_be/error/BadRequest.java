package com.officina.officina_be.error;

public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}
