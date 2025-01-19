package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.entity.TicketStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * TicketDto - Data Transfer Object representing information about a ticket.
 */
@Value
@Builder
public class TicketDto {
    TicketStatus status;
    BigDecimal price;
    SportEvent sportEvent;
    Seat seat;
}
