package com.example.ticketbookingsystem.mapper.ticket_mapper;

import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TicketReadMapper {

    TicketReadMapper INSTANCE = Mappers.getMapper(TicketReadMapper.class);

    TicketReadDto toDto(Ticket ticket);

    Ticket toEntity(TicketReadDto ticketReadDto);
}