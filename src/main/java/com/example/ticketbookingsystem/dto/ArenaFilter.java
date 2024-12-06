package com.example.ticketbookingsystem.dto;

public record ArenaFilter (
        String city,
        String capacitySortOrder,
        String seatsNumbSortOrder,
        int limit,
        int offset
){}