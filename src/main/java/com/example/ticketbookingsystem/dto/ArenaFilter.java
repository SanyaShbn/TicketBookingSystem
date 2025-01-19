package com.example.ticketbookingsystem.dto;

/**
 * ArenaFilter - Data Transfer Object for managing arenas list filtration parameters.
 */
public record ArenaFilter (
        String city,
        String capacitySortOrder,
        String seatsNumbSortOrder,
        int limit,
        int offset
){}