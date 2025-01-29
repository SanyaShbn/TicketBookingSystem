package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.validator.PasswordMatches;
import com.example.ticketbookingsystem.validator.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * UserDto - Data Transfer Object representing information about a user.
 */
@Value
@Builder
@PasswordMatches
public class UserDto {
    Long id;

    @ValidEmail
    @NotNull
    @NotEmpty
    String email;

    @NotNull
    @NotEmpty
    String password;

    String confirmPassword;

    String role;
}
