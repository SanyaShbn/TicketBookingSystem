package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.Builder;
import lombok.Value;

/**
 * SectorDto - Data Transfer Object representing information about a sector.
 */
@Value
@Builder
public class SectorDto {
    String sectorName;
    Arena arena;
    int maxRowsNumb;
    int maxSeatsNumb;
}
