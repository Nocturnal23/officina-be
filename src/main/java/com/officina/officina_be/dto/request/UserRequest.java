package com.officina.officina_be.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotBlank(message = "Devi obbligatoriamente inserire una mail")
    @Email(message = "Formato email non valida")
    String email,

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    String password,

    @NotBlank(message = "Il nome è obbligatorio")
    String firstName,

    @NotBlank(message = "Il cognome è obbligatorio")
    String lastName,

    @NotBlank(message = "Il numero di telefono è obbligatorio")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Formato numero di telefono non valido")
    String phoneNumber
) {}
