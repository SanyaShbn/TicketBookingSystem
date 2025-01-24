package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

/**
 * ArenaCreateEditDto - Data Transfer Object representing information about an arena when creating or updating arena.
 */
@Value
@Builder
public class ArenaCreateEditDto {
    String name;
    String city;
    int capacity;
}
