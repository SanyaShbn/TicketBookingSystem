package com.example.ticketbookingsystem.dto.sport_event_dto;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * SportEventDto - Data Transfer Object representing information about a sport event.
 */
@Value
@Builder
public class SportEventReadDto {
    Long id;
    String eventName;
    LocalDateTime eventDateTime;
    Arena arena;
}
