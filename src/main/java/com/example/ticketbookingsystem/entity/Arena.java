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

    public Arena (String name, String city, int capacity){
        this.name = name;
        this.city = city;
        this.capacity = capacity;
    }
}
