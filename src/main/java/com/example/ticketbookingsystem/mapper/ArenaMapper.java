//package com.example.ticketbookingsystem.mapper;
//
//import com.example.ticketbookingsystem.dto.ArenaReadDto;
//import com.example.ticketbookingsystem.entity.Arena;
//import lombok.NoArgsConstructor;
//import org.springframework.stereotype.Component;
//
///**
// * Mapper class for converting between {@link Arena} entity and {@link ArenaReadDto} DTO.
// */
//@Component
//@NoArgsConstructor
//public class ArenaMapper implements Mapper<Arena, ArenaReadDto> {
//
//    /**
//     * Converts an {@link ArenaReadDto} to an {@link Arena} entity.
//     *
//     * @param arenaDto the DTO to convert
//     * @return the converted {@link Arena} entity
//     */
//    @Override
//    public Arena toEntity(ArenaReadDto arenaDto) {
//        return Arena.builder()
//                .id(arenaDto.getId())
//                .name(arenaDto.getName())
//                .city(arenaDto.getCity())
//                .capacity(arenaDto.getCapacity())
//                .generalSeatsNumb(arenaDto.getGeneralSeatsNumb())
//                .build();
//    }
//
//    /**
//     * Converts an {@link Arena} entity to an {@link ArenaReadDto}.
//     *
//     * @param arena the entity to convert
//     * @return the converted {@link ArenaReadDto}
//     */
//    @Override
//    public ArenaReadDto toDto(Arena arena) {
//        return ArenaReadDto.builder()
//                .id(arena.getId())
//                .name(arena.getName())
//                .city(arena.getCity())
//                .capacity(arena.getCapacity())
//                .generalSeatsNumb(arena.getGeneralSeatsNumb())
//                .build();
//    }
//}
//
