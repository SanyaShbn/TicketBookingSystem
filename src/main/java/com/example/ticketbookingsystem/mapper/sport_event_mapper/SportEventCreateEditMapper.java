package com.example.ticketbookingsystem.mapper.sport_event_mapper;

import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.entity.SportEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SportEventCreateEditMapper {
    SportEventCreateEditMapper INSTANCE = Mappers.getMapper(SportEventCreateEditMapper.class);

    SportEvent toEntity(SportEventCreateEditDto sportEventCreateEditDto);

    SportEventCreateEditDto toDto(SportEvent sportEvent);
}
