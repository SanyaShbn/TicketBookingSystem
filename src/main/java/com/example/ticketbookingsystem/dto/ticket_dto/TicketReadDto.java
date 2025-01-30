package com.example.ticketbookingsystem.dto.ticket_dto;

import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.entity.TicketStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class TicketReadDto {
    Long id;
    TicketStatus status;
    BigDecimal price;
    SportEvent sportEvent;
    Seat seat;
}
