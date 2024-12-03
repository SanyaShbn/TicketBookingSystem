package com.example.ticketbookingsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Arena {
    private Long id;
    private String name;
    private String city;
    private int capacity;
    private int generalSeatsNumb;
}
