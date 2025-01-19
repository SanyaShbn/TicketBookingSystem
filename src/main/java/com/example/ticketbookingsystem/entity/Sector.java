package com.example.ticketbookingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents arena's sectors (collection of rows and seats in the specific part of the arena) in the system
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sector_name", nullable = false)
    private String sectorName;

    @Column(name = "max_rows_numb", nullable = false)
    private int maxRowsNumb;

    @Column(name = "available_rows_numb", nullable = false)
    private int availableRowsNumb;

    @Column(name = "max_seats_numb", nullable = false)
    private int maxSeatsNumb;

    @Column(name = "available_seats_numb", nullable = false)
    private int availableSeatsNumb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arena_id", nullable = false)
    private Arena arena;
}
