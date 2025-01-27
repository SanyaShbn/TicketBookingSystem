package com.example.ticketbookingsystem.dto;

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
