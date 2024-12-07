package com.example.ticketbookingsystem.dto;

public record RowFilter(
        String rowNumberOrder,
        String seatsNumbOrder,
        int limit,
        int offset
){}
