package com.example.ticketbookingsystem.dto.arena_dto;

import com.example.ticketbookingsystem.validator.custom_annotations.CapacityCheck;
import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import lombok.Builder;
import lombok.Value;

/**
 * ArenaCreateEditDto - Data Transfer Object representing information about an arena
 * when creating or updating arena.
 */
@Value
@Builder
public class ArenaCreateEditDto {

    @LocalizedNotBlank
    String name;

    @LocalizedNotBlank
    String city;

    @CapacityCheck(min = 1, max = 22000)
    Integer capacity;
}
