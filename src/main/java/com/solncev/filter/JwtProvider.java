package com.solncev.filter;

import com.solncev.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(@Value("${jwt.secret.access}") String jwtAccessSecret,
                       @Value("${jwt.secret.refresh}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationTime = now.plusHours(1).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(Date.from(accessExpirationTime))
                .signWith(jwtAccessSecret)
                .claim("roles", user.getRoles())
                .claim("email", user.getEmail())
                .compact();
    }


    public String generateRefreshToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        Instant refreshExpirationTime = now.plusDays(1).atZone(ZoneId.systemDefault()).toInstant();
        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(Date.from(refreshExpirationTime))
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    public Claims getAccessClaims(String token) {
        return getClaimsFromToken(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String token) {
        return getClaimsFromToken(token, jwtRefreshSecret);
    }

    private Claims getClaimsFromToken(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    private boolean validateToken(String token, SecretKey secretKey) {
        try {
            getClaimsFromToken(token, secretKey);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
