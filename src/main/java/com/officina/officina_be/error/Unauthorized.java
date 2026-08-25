package com.officina.officina_be.error;

public class Unauthorized extends RuntimeException {
    public Unauthorized(String message) {
        super(message);
    }
}