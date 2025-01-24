package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.entity.Arena;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArenaCreateEditMapper {
    ArenaCreateEditMapper INSTANCE = Mappers.getMapper(ArenaCreateEditMapper.class);

    Arena toEntity(ArenaCreateEditDto arenaCreateEditDto);
    ArenaCreateEditDto toDto(Arena arena);
}