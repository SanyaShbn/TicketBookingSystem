package com.example.ticketbookingsystem.dto.sector_dto;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.Builder;
import lombok.Value;

/**
 * SectorReadDto - Data Transfer Object representing information about a sector when reading.
 */
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
