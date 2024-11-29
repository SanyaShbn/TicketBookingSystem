package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ArenaDto {
    String name;
    String city;
    int capacity;
}
