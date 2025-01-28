package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TicketCreateEditMapper {

    TicketCreateEditMapper INSTANCE = Mappers.getMapper(TicketCreateEditMapper.class);

    Ticket toEntity(TicketCreateEditDto ticketCreateEditDto);

    TicketCreateEditDto toDto(Ticket ticket);
}
