package com.example.ticketbookingsystem.dto.sport_event_dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class SportEventCreateEditDto {
    String eventName;
    LocalDateTime eventDateTime;
}
