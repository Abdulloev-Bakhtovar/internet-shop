package ru.bakht.internetshop.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.service.JwtService;
import ru.bakht.internetshop.auth.service.TokenBlacklistService;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (isExcludedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractEmail(jwt);

        if (!jwtService.isAccessToken(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only access tokens are allowed");
            return;
        }

        if (tokenBlacklistService.isBlacklisted(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if(!jwtService.isTokenExpired(jwt)) {
                var userId = jwtService.extractUserId(jwt);
                var authorities = jwtService.extractAuthorities(jwt);
                var authenticatedUser = User.builder()
                        .id(UUID.fromString(userId))
                        .email(userEmail)
                        .build();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        authorities
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcludedPath(String requestURI) {
        return securityProperties.getExcludedPaths().stream()
                .anyMatch(requestURI::startsWith);
    }
}

