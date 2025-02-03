package com.example.ticketbookingsystem.dto.sector_dto;

/**
 * SectorFilter - Data Transfer Object for managing sectors list filtration parameters.
 */
public record SectorFilter  (
        String nameSortOrder,
        String maxRowsNumbSortOrder,
        String maxSeatsNumbSortOrder
){}