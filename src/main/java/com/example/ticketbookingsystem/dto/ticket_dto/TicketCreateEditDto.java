package com.example.ticketbookingsystem.dto.ticket_dto;

import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import com.example.ticketbookingsystem.validator.custom_annotations.ValidPrice;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class TicketCreateEditDto {

    TicketStatus status;

    @ValidPrice
    BigDecimal price;
}
