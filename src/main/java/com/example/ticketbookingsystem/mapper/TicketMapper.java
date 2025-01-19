package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.TicketDto;
import com.example.ticketbookingsystem.entity.Ticket;

/**
 * Mapper class for converting between {@link Ticket} entity and {@link TicketDto} DTO.
 */
public class TicketMapper implements Mapper<Ticket, TicketDto> {

    private static final TicketMapper INSTANCE = new TicketMapper();

    private TicketMapper(){}
    public static TicketMapper getInstance(){
        return INSTANCE;
    }

    /**
     * Converts an {@link TicketDto} to an {@link Ticket} entity.
     *
     * @param ticketDto the DTO to convert
     * @return the converted {@link Ticket} entity
     */
    @Override
    public Ticket toEntity(TicketDto ticketDto) {
        return Ticket.builder()
                .status(ticketDto.getStatus())
                .price(ticketDto.getPrice())
                .sportEvent(ticketDto.getSportEvent())
                .seat(ticketDto.getSeat())
                .build();
    }

    /**
     * Converts an {@link Ticket} entity to an {@link TicketDto}.
     *
     * @param ticket the entity to convert
     * @return the converted {@link TicketDto}
     */
    @Override
    public TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .status(ticket.getStatus())
                .price(ticket.getPrice())
                .sportEvent(ticket.getSportEvent())
                .seat(ticket.getSeat())
                .build();
    }
}