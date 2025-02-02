package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import com.example.ticketbookingsystem.validator.custom_annotations.PasswordMatches;
import com.example.ticketbookingsystem.validator.custom_annotations.ValidEmail;
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
    @LocalizedNotBlank
    String email;

    @LocalizedNotBlank
    String password;

    @LocalizedNotBlank
    String confirmPassword;

    String role;
}
