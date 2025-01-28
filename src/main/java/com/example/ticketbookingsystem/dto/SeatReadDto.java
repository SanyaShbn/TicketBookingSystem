package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Row;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SeatReadDto {
    Long id;
    int seatNumber;
    Row row;
}
