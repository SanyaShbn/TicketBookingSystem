package com.example.ticketbookingsystem.mapper.arena_mapper;

import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Arena entities and ArenaReadDto.
 */
@Mapper(componentModel = "spring")
public interface ArenaReadMapper {
    ArenaReadMapper INSTANCE = Mappers.getMapper(ArenaReadMapper.class);

    ArenaReadDto toDto(Arena arena);

    Arena toEntity(ArenaReadDto arenaDto);
}