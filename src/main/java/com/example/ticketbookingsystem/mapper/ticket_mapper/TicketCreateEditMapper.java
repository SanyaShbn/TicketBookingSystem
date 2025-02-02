package com.example.ticketbookingsystem.mapper.ticket_mapper;

import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Ticket entities and TicketCreateEditDto.
 */
@Mapper(componentModel = "spring")
public interface TicketCreateEditMapper {

    TicketCreateEditMapper INSTANCE = Mappers.getMapper(TicketCreateEditMapper.class);

    Ticket toEntity(TicketCreateEditDto ticketCreateEditDto);

    TicketCreateEditDto toDto(Ticket ticket);
}
