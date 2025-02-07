package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.security.AccountCredentials;
import com.example.ticketbookingsystem.security.JwtService;
import com.example.ticketbookingsystem.security.RefreshToken;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>>  getToken(@RequestBody AccountCredentials credentials) {
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

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (jwtService.validateRefreshToken(refreshToken)) {
            RefreshToken token = jwtService.getRefreshToken(refreshToken);
            List<GrantedAuthority> roles = (List<GrantedAuthority>) jwtService.getRolesForUser(token.getUsername());
            String newAccessToken = jwtService.getToken(token.getUsername(), roles);
            String newRefreshToken = jwtService.generateRefreshToken(token.getUsername());

            return getMapResponseEntity(newAccessToken, newRefreshToken);
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }

    @NotNull
    private ResponseEntity<Map<String, String>> getMapResponseEntity(String newAccessToken, String newRefreshToken) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                .header("Refresh-Token", newRefreshToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization, Refresh-Token")
                .body(tokens);
    }
}