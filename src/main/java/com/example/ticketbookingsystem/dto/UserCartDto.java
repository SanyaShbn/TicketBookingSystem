package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

/**
 * UserCartDto - Data Transfer Object representing information about a user cart.
 */
@Value
@Builder
public class UserCartDto {
    Long userId;
    Long ticketId;
}
