package com.officina.officina_be.dto.response;

import com.officina.officina_be.core.utils.Constants.Role;

public record UserResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    Role role
) {}
