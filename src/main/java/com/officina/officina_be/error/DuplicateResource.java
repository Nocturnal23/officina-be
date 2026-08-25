package com.officina.officina_be.error;

public class DuplicateResource extends RuntimeException {
    public DuplicateResource(String message) {
        super(message);
    }
}