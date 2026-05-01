package com.solncev.filter;

import com.solncev.entity.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setRoles(getRoles(claims));
        jwtAuthentication.setUsername(claims.getSubject());
        return jwtAuthentication;
    }

    private static Set<Role> getRoles(Claims claims) {
        Object rolesClaim = claims.get("roles");
        if (!(rolesClaim instanceof Collection<?> roles)) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(String::valueOf)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = getToken((HttpServletRequest) request);
        if (token != null) {
            if (jwtProvider.validateAccessToken(token)) {
                Claims claims = jwtProvider.getAccessClaims(token);
                JwtAuthentication jwtAuthentication = generate(claims);
                jwtAuthentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
            }
        }
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
