package com.example.ticketbookingsystem.security;

import com.example.ticketbookingsystem.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the account credentials of a user.
 */
@Getter
@Setter
public class AccountCredentials {
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
}
