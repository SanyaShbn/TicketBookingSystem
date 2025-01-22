package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.ArenaDto;
import com.example.ticketbookingsystem.entity.Arena;

/**
 * Mapper class for converting between {@link Arena} entity and {@link ArenaDto} DTO.
 */
public class ArenaMapper implements Mapper<Arena, ArenaDto> {

    private static final ArenaMapper INSTANCE = new ArenaMapper();

    private ArenaMapper(){}
    public static ArenaMapper getInstance(){
        return INSTANCE;
    }

    /**
     * Converts an {@link ArenaDto} to an {@link Arena} entity.
     *
     * @param arenaDto the DTO to convert
     * @return the converted {@link Arena} entity
     */
    @Override
    public Arena toEntity(ArenaDto arenaDto) {
//        return Arena.builder()
//                .name(arenaDto.getName())
//                .city(arenaDto.getCity())
//                .capacity(arenaDto.getCapacity())
//                .build();
        return new Arena();
    }

    /**
     * Converts an {@link Arena} entity to an {@link ArenaDto}.
     *
     * @param arena the entity to convert
     * @return the converted {@link ArenaDto}
     */
    @Override
    public ArenaDto toDto(Arena arena) {
        return ArenaDto.builder()
                .name(arena.getName())
                .city(arena.getCity())
                .capacity(arena.getCapacity())
                .build();
    }
}

