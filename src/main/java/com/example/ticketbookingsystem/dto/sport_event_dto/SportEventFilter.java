package com.example.ticketbookingsystem.dto.sport_event_dto;

import java.time.LocalDateTime;

/**
 * SportEventFilter - Data Transfer Object for managing sport events list filtration parameters.
 */
public record SportEventFilter (
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long arenaId,
        String sortOrder
){}
