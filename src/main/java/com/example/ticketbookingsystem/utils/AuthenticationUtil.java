package com.example.ticketbookingsystem.utils;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility class for handling authentication-related operations.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserService userService;

    /**
     * Gets the authenticated user from the security context.
     *
     * @return An {@link Optional} containing the authenticated user DTO, or empty if not authenticated.
     * @throws UsernameNotFoundException If the user is not found.
     */
    public Optional<UserDto> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.findByEmail(userDetails.getUsername());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}