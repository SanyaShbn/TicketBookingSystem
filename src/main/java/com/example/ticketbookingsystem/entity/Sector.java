package com.example.ticketbookingsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sector {
    private Long id;
    private String sectorName;
    private Arena arena;
    private int maxRowsNumb;
    private int availableRowsNumb;
    private int maxSeatsNumb;
    private int availableSeatsNumb;
}
