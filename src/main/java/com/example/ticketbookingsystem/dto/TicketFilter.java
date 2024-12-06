package com.example.ticketbookingsystem.dto;

public record TicketFilter  (
        String priceSortOrder,
        int limit,
        int offset
){}
