package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class SportEventCreateEditDto {
    String eventName;
    LocalDateTime eventDateTime;
}
