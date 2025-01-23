package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

/**
 * ArenaDto - Data Transfer Object representing information about an arena.
 */
@Value
@Builder
public class ArenaDto {
    Long id;
    String name;
    String city;
    int capacity;
    int generalSeatsNumb;
}
