package com.officina.officina_be.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JobRequest(
    @NotBlank(message = "Devi obbligatoriamente dare un nome al servizio!")
    String type,

    @NotBlank(message = "Devi obbligatoriamente dare una descrizione al servizio!")
    String description
) {}
