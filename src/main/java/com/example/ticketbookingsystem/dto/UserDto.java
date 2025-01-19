package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

/**
 * UserDto - Data Transfer Object representing information about a user.
 */
@Value
@Builder
public class UserDto {
    Long id;
    String email;
    String password;
    String role;
    String confirmPassword;
}
