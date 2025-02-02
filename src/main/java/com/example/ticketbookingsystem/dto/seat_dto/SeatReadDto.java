package com.example.ticketbookingsystem.dto.seat_dto;

import com.example.ticketbookingsystem.entity.Row;
import lombok.Builder;
import lombok.Value;

/**
 * SeatReadDto - Data Transfer Object representing information about a seat when reading.
 */
@Value
@Builder
public class SeatReadDto {
    Long id;
    int seatNumber;
    Row row;
}
