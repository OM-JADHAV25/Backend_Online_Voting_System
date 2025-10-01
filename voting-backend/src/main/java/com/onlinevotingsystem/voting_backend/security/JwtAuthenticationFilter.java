package com.onlinevotingsystem.voting_backend.security;

import com.onlinevotingsystem.voting_backend.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * JWT Authentication Filter for validating tokens sent in Authorization header
 * Adapted to use voterId instead of email for OTP login flow
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // Public endpoints that should skip JWT validation
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/voters/send-otp",
            "/api/voters/verify-otp",
            "/api/users/register",
            "/api/users/request-otp",
            "/api/users/verify-otp",
            "/api/auth/",
            "/api/admin/login",
            "/api/test/"

    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        logger.info("Processing request: {} {}", request.getMethod(), requestPath);

        // Skip JWT validation for public endpoints
        if (isPublicPath(requestPath)) {
            logger.info("Public path detected, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String identifier = null;
        String jwt = null;

        // Special handling for admin endpoints
        if (requestPath.startsWith("/api/admin/")) {
            logger.info("Admin endpoint detected: {}", requestPath);
            try {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                    identifier = jwtUtil.extractVoterId(jwt);

                    logger.info("Extracted identifier from token: {}", identifier);

                    if (identifier != null && jwtUtil.validateToken(jwt, identifier)) {
                        logger.info("Token validated successfully for admin: {}", identifier);

                        // Create simple authentication without UserDetailsService
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(identifier, null,
                                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        logger.info("Authentication set successfully for admin");
                    } else {
                        logger.error("Token validation failed for identifier: {}", identifier);
                    }
                } else {
                    logger.error("No Bearer token found in Authorization header");
                }
            } catch (Exception e) {
                logger.error("Cannot set admin authentication: {}", e.getMessage(), e);
            }
            filterChain.doFilter(request, response);
            return;
        }

        // Standard user authentication flow
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                identifier = jwtUtil.extractVoterId(jwt);
            }

            if (identifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(identifier);

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request path is a public endpoint
     */
    private boolean isPublicPath(String requestPath) {
        return PUBLIC_PATHS.stream().anyMatch(requestPath::startsWith);
    }
}