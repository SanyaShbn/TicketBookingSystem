package com.example.ticketbookingsystem.dto;

import java.time.LocalDateTime;

public record SportEventFilter (
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long arenaId,
        int limit,
        int offset,
        String sortOrder
){}
