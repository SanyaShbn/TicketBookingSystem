package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * SportEventDto - Data Transfer Object representing information about a sport event.
 */
@Value
@Builder
public class SportEventDto {
    String eventName;
    LocalDateTime eventDateTime;
    Arena arena;
}
