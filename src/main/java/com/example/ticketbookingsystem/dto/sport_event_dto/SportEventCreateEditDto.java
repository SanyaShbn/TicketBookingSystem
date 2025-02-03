package com.example.ticketbookingsystem.dto.sport_event_dto;

import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import com.example.ticketbookingsystem.validator.custom_annotations.ValidEventDateTime;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * SportEventCreateEditDto - Data Transfer Object representing information about a sport event
 * when creating or updating sport event.
 */
@Value
@Builder
public class SportEventCreateEditDto {

    @LocalizedNotBlank
    String eventName;

    @ValidEventDateTime
    LocalDateTime eventDateTime;
}
