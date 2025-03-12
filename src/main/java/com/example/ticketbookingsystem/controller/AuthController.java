package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.security.AccountCredentials;
import com.example.ticketbookingsystem.security.JwtService;
import com.example.ticketbookingsystem.security.RefreshToken;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing login and token refresh operations.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    private static final String ACCESS_TOKEN_KEY = "accessToken";

    private static final String REFRESH_TOKEN_HEADER = "Refresh-Token";

    private static final String EXPOSE_HEADERS = "Authorization, Refresh-Token";

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates the user and returns JWT tokens.
     *
     * @param credentials the account credentials of the user.
     * @return a ResponseEntity containing the access and refresh tokens.
     */
    @RequestMapping(value="/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> getToken(@RequestBody AccountCredentials credentials) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword()
                );

        Authentication auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String accessToken = jwtService.getToken(auth.getName(), auth.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(auth.getName());

        return getMapResponseEntity(accessToken, refreshToken);
    }

    /**
     * Refreshes the JWT tokens using the provided refresh token.
     *
     * @param body a map containing the refresh token.
     * @return a ResponseEntity containing the new access and refresh tokens.
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get(REFRESH_TOKEN_KEY);
        if (jwtService.validateRefreshToken(refreshToken)) {
            RefreshToken token = jwtService.getRefreshToken(refreshToken);
            List<GrantedAuthority> roles = jwtService.getRolesForUser(token.getUsername());
            String newAccessToken = jwtService.getToken(token.getUsername(), roles);
            String newRefreshToken = jwtService.generateRefreshToken(token.getUsername());

            return getMapResponseEntity(newAccessToken, newRefreshToken);
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }

    /**
     * Creates a ResponseEntity with the provided access and refresh tokens.
     *
     * @param newAccessToken the new access token.
     * @param newRefreshToken the new refresh token.
     * @return a ResponseEntity containing the tokens.
     */
    @NotNull
    private ResponseEntity<Map<String, String>> getMapResponseEntity(String newAccessToken, String newRefreshToken) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN_KEY, newAccessToken);
        tokens.put(REFRESH_TOKEN_KEY, newRefreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                .header(REFRESH_TOKEN_HEADER, newRefreshToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, EXPOSE_HEADERS)
                .body(tokens);
    }

    /**
     * Handles user logout request and invalidates the refresh token (access token remains valid until it expires).
     *
     * @param refreshToken the new refresh token.
     * @return a ResponseEntity with no content if logout was successful or with "unauthorized" response - if not.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<Void> logout(@RequestHeader("Refresh-Token") String refreshToken) {
        if (jwtService.validateRefreshToken(refreshToken)) {
            jwtService.invalidateRefreshToken(refreshToken);
            log.info("Successfully logged out user with refresh token: {}", refreshToken);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Invalid refresh token provided during logout");
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }
}