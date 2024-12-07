package com.example.ticketbookingsystem.dto;

public record SectorFilter  (
        String nameSortOrder,
        String maxRowsNumbSortOrder,
        String maxSeatsNumbSortOrder,
        int limit,
        int offset
){}