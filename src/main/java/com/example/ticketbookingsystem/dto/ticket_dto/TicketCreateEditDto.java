package com.example.ticketbookingsystem.dto.ticket_dto;

import com.example.ticketbookingsystem.entity.TicketStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class TicketCreateEditDto {
    TicketStatus status;
    BigDecimal price;
}
