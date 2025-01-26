package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Sector;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RowReadDto {
    Long id;
    int rowNumber;
    int seatsNumb;
    Sector sector;
}
