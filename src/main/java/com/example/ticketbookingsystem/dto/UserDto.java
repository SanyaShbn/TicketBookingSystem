package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    Long id;
    String email;
    String password;
    String confirmPassword;
}
