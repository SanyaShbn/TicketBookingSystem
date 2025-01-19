package com.example.ticketbookingsystem.dto;

/**
 * SectorFilter - Data Transfer Object for managing sectors list filtration parameters.
 */
public record SectorFilter  (
        String nameSortOrder,
        String maxRowsNumbSortOrder,
        String maxSeatsNumbSortOrder,
        int limit,
        int offset
){}