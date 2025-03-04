package com.example.ticketbookingsystem.mapper.arena_mapper;

import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.entity.Arena;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Arena entities and ArenaCreateEditDto.
 */
@Mapper(componentModel = "spring")
public interface ArenaCreateEditMapper {
    ArenaCreateEditMapper INSTANCE = Mappers.getMapper(ArenaCreateEditMapper.class);

    Arena toEntity(ArenaCreateEditDto arenaCreateEditDto);

    ArenaCreateEditDto toDto(Arena arena);

    void updateEntityFromDto(ArenaCreateEditDto arenaCreateEditDto, @MappingTarget Arena arena);
}