package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

/**
 * ArenaReadDto - Data Transfer Object representing information about an arena when reading.
 */
@Value
@Builder
public class ArenaReadDto {
    Long id;
    String name;
    String city;
    int capacity;
    int generalSeatsNumb;
}
