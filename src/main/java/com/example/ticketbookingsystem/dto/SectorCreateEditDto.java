package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SectorCreateEditDto {
    String sectorName;
    int maxRowsNumb;
    int maxSeatsNumb;
}
