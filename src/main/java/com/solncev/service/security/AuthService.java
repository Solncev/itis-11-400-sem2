package com.solncev.service.security;

import com.solncev.dto.security.JwtRefreshRequest;
import com.solncev.dto.security.JwtRequest;
import com.solncev.dto.security.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest jwtRequest);
    JwtResponse refresh(JwtRefreshRequest jwtRefreshRequest);
    JwtResponse token(JwtRefreshRequest jwtRefreshRequest);
}
