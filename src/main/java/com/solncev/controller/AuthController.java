package com.solncev.controller;

import com.solncev.dto.security.JwtRefreshRequest;
import com.solncev.dto.security.JwtRequest;
import com.solncev.dto.security.JwtResponse;
import com.solncev.service.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest){
        return ResponseEntity.ok(authService.login(jwtRequest));
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> token(@RequestBody JwtRefreshRequest jwtRequest) {
        return ResponseEntity.ok(authService.token(jwtRequest));
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody JwtRefreshRequest jwtRequest) {
        return ResponseEntity.ok(authService.refresh(jwtRequest));
    }
}