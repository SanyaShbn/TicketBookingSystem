package com.example.ticketbookingsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Row {
    private Long id;
    private int rowNumber;
    private int seatsNumb;
    private Sector sector;
}
