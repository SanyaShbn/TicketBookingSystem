package com.example.ticketbookingsystem.dto.arena_dto;

/**
 * ArenaFilter - Data Transfer Object for managing arenas list filtration parameters.
 */
public record ArenaFilter (
        String city,
        String capacitySortOrder,
        String seatsNumbSortOrder
){}