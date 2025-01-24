package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.ArenaReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArenaReadMapper {
    ArenaReadMapper INSTANCE = Mappers.getMapper(ArenaReadMapper.class);

    ArenaReadDto toDto(Arena arena);

    Arena toEntity(ArenaReadDto arenaDto);
}