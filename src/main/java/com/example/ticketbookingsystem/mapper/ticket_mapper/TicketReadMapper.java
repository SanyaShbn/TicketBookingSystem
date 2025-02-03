package com.example.ticketbookingsystem.mapper.ticket_mapper;

import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Ticket entities and TicketReadDto.
 */
@Mapper(componentModel = "spring")
public interface TicketReadMapper {

    TicketReadMapper INSTANCE = Mappers.getMapper(TicketReadMapper.class);

    TicketReadDto toDto(Ticket ticket);

    Ticket toEntity(TicketReadDto ticketReadDto);
}