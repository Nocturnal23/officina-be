package com.officina.officina_be.dto.response;

public record LoginResponse(
    String token,
    UserResponse user
) {}
