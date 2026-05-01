package com.solncev.service.security;

import com.solncev.dto.security.JwtRefreshRequest;
import com.solncev.dto.security.JwtRequest;
import com.solncev.dto.security.JwtResponse;
import com.solncev.filter.JwtProvider;
import com.solncev.entity.User;
import com.solncev.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final Map<String, String> refreshStorage = new ConcurrentHashMap<>();

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }


    @Override
    public JwtResponse login(JwtRequest jwtRequest) {
        User user = userRepository.findByUsername(jwtRequest.login())
                .orElseThrow(() -> new UsernameNotFoundException(jwtRequest.login()));

        if (!passwordEncoder.matches(jwtRequest.password(), user.getPassword())) {
            throw new BadCredentialsException(jwtRequest.login());
        }
        if (!user.isEnabled()) {
            throw new DisabledException("Account is not verified");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        refreshStorage.put(jwtRequest.login(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse refresh(JwtRefreshRequest jwtRefreshRequest) {
        if (jwtProvider.validateRefreshToken(jwtRefreshRequest.token())) {
            Claims claims = jwtProvider.getRefreshClaims(jwtRefreshRequest.token());
            String username = claims.getSubject();
            String refreshToken = refreshStorage.get(username);
            if (refreshToken != null && refreshToken.equals(jwtRefreshRequest.token())) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username));
                String accessToken = jwtProvider.generateAccessToken(user);
                String newRefresh = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(username, newRefresh);
                return new JwtResponse(accessToken, newRefresh);
            }
        }
        throw new BadCredentialsException("Invalid refresh token");
    }

    @Override
    public JwtResponse token(JwtRefreshRequest jwtRefreshRequest) {
        if (!jwtProvider.validateRefreshToken(jwtRefreshRequest.token())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        Claims claims = jwtProvider.getRefreshClaims(jwtRefreshRequest.token());
        String username = claims.getSubject();
        String storedRefreshToken = refreshStorage.get(username);
        if (storedRefreshToken == null || !storedRefreshToken.equals(jwtRefreshRequest.token())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        String accessToken = jwtProvider.generateAccessToken(user);
        return new JwtResponse(accessToken, storedRefreshToken);
    }
}
