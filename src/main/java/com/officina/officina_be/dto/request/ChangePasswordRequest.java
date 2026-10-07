package com.officina.officina_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "La vecchia password è obbligatoria") 
    String oldPassword,
    
    @NotBlank(message = "La nuova password è obbligatoria") 
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    String newPassword
) {
}