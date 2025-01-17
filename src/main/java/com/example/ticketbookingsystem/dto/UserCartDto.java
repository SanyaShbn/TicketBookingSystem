package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCartDto {
    Long userId;
    Long ticketId;
}
