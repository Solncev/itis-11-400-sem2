package com.solncev.service.security;

import com.solncev.dto.security.JwtRefreshRequest;
import com.solncev.dto.security.JwtRequest;
import com.solncev.dto.security.JwtResponse;
import jakarta.security.auth.message.AuthException;

public interface AuthService {
    JwtResponse login(JwtRequest jwtRequest);
    JwtResponse refresh(JwtRefreshRequest jwtRefreshRequest) throws AuthException;
    JwtResponse token(JwtRefreshRequest jwtRefreshRequest);
}
