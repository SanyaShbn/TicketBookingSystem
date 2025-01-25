package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SectorReadDto {
    Long id;
    String sectorName;
    int maxRowsNumb;
    int availableRowsNumb;
    int maxSeatsNumb;
    int availableSeatsNumb;
    Arena arena;
}
