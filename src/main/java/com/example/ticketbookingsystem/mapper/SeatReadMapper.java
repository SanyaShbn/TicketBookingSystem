package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.SeatReadDto;
import com.example.ticketbookingsystem.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SeatReadMapper {
    SeatReadMapper INSTANCE = Mappers.getMapper(SeatReadMapper.class);

    SeatReadDto toDto(Seat seat);

    Seat toEntity(SeatReadDto seatReadDto);
}
