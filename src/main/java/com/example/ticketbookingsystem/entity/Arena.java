package com.example.ticketbookingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Represents an arena in the ticket booking system where sporting events can be held
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Arena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "general_seats_numb")
    private int generalSeatsNumb;
}
