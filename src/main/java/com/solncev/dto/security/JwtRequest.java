package com.solncev.dto.security;

public record JwtRequest(
        String login,
        String password
) {}
