package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class SportEventDto {
    String eventName;
    LocalDateTime eventDateTime;
    Arena arena;
}
