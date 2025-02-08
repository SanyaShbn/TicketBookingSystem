package com.example.ticketbookingsystem.security;

import com.example.ticketbookingsystem.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for handling JWT operations.
 */
@Component
@RequiredArgsConstructor
public class JwtService {

    static final long ACCESS_TOKEN_EXPIRATION_TIME = 900000;

    static final long REFRESH_TOKEN_EXPIRATION_TIME = 3600000;

    static final String PREFIX = "Bearer";

    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserDetailsService userDetailsService;

    /**
     * Generates an access token for the given username and roles.
     *
     * @param username the username of the user.
     * @param roles the roles of the user.
     * @return the generated access token.
     */
    public String getToken(String username, Collection<? extends GrantedAuthority> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Generates a refresh token for the given username.
     *
     * @param username the username of the user.
     * @return the generated refresh token.
     */
    public String generateRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setUsername(username);
        token.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        refreshTokenRepository.save(token);
        return refreshToken;
    }

    /**
     * Retrieves the authentication information from the given request.
     *
     * @param request the HTTP request.
     * @return the authentication information.
     */
    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody();

            String user = claims.getSubject();
            var roles = (List<String>) claims.get("roles");
            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
        }
        return null;
    }

    /**
     * Validates the given refresh token (whether it exists at all and not expired if exists).
     *
     * @param refreshToken the refresh token to validate.
     * @return true if the refresh token is valid, false otherwise.
     */
    public boolean validateRefreshToken(String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        return tokenOpt.isPresent() && tokenOpt.get().getExpiryDate().isAfter(Instant.now());
    }

    /**
     * Retrieves the refresh token information from the repository.
     *
     * @param refreshToken the refresh token.
     * @return the refresh token information.
     */
    public RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    }

    /**
     * Retrieves the roles for the given username.
     *
     * @param username the username of the user.
     * @return the list of granted authorities (roles) for the user.
     */
    public List<GrantedAuthority> getRolesForUser(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new ArrayList<>(userDetails.getAuthorities());
    }

}
