package com.example.ticketbookingsystem.dto;

/**
 * TicketFilter - Data Transfer Object for managing tickets list filtration parameters.
 */
public record TicketFilter  (
        String priceSortOrder,
        int limit,
        int offset
){}
