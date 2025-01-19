package com.example.ticketbookingsystem.dto;

/**
 * RowFilter - Data Transfer Object for managing rows list filtration parameters.
 */
public record RowFilter(
        String rowNumberOrder,
        String seatsNumbOrder,
        int limit,
        int offset
){}
