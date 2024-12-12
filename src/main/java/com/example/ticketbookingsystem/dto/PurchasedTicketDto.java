package com.example.ticketbookingsystem.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
