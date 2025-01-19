package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PurchasedTicketDto - Data Transfer Object representing information about a purchased ticket.
 */
@Value
@Builder
public class PurchasedTicketDto {
    Long ticketId;
    String eventName;
    LocalDateTime eventDateTime;
    String arenaName;
    String arenaCity;
    String sectorName;
    Integer rowNumber;
    Integer seatNumber;
    BigDecimal price;
}
