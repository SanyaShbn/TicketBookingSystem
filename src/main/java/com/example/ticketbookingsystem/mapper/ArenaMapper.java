package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.ArenaDto;
import com.example.ticketbookingsystem.entity.Arena;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between {@link Arena} entity and {@link ArenaDto} DTO.
 */
@Component
@NoArgsConstructor
public class ArenaMapper implements Mapper<Arena, ArenaDto> {

    /**
     * Converts an {@link ArenaDto} to an {@link Arena} entity.
     *
     * @param arenaDto the DTO to convert
     * @return the converted {@link Arena} entity
     */
    @Override
    public Arena toEntity(ArenaDto arenaDto) {
        return Arena.builder()
                .id(arenaDto.getId())
                .name(arenaDto.getName())
                .city(arenaDto.getCity())
                .capacity(arenaDto.getCapacity())
                .generalSeatsNumb(arenaDto.getGeneralSeatsNumb())
                .build();
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
                .id(arena.getId())
                .name(arena.getName())
                .city(arena.getCity())
                .capacity(arena.getCapacity())
                .generalSeatsNumb(arena.getGeneralSeatsNumb())
                .build();
    }
}

