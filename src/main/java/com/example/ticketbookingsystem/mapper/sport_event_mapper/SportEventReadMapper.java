package com.example.ticketbookingsystem.mapper.sport_event_mapper;

import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.entity.SportEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SportEventReadMapper {

    SportEventReadMapper INSTANCE = Mappers.getMapper(SportEventReadMapper.class);

    SportEventReadDto toDto(SportEvent sportEvent);

    SportEvent toEntity(SportEventReadDto sportEventReadDto);
}
