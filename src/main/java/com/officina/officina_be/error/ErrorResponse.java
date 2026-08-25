package com.officina.officina_be.error;

import java.time.LocalDateTime;

public record ErrorResponse(
    int statusCode,           // es. 404, 409, 400
    String error,             // Descrizione HTTP (es. "Not Found", "Conflict")
    String message,           // Messaggio dettagliato per il frontend
    String path,              // L'endpoint chiamato (es. "/api/users/register")
    LocalDateTime timestamp  // Data e ora dell'errore
) {
    public ErrorResponse(int statusCode, String error, String message, String path) {
        this(statusCode, error, message, path, LocalDateTime.now());
    }
}
