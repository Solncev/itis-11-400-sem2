package com.solncev.dto.security;

public record JwtResponse(
        String accessToken,
        String refreshToken
) {
}
