package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.SportEventDto;
import com.example.ticketbookingsystem.entity.SportEvent;

/**
 * Mapper class for converting between {@link SportEvent} entity and {@link SportEventDto} DTO.
 */
public class SportEventMapper implements Mapper<SportEvent, SportEventDto> {

    private static final SportEventMapper INSTANCE = new SportEventMapper();

    private SportEventMapper(){}
    public static SportEventMapper getInstance(){
        return INSTANCE;
    }

    /**
     * Converts an {@link SportEventDto} to an {@link SportEvent} entity.
     *
     * @param sportEventDto the DTO to convert
     * @return the converted {@link SportEvent} entity
     */
    @Override
    public SportEvent toEntity(SportEventDto sportEventDto) {
        return SportEvent.builder()
                .eventName(sportEventDto.getEventName())
                .eventDateTime(sportEventDto.getEventDateTime())
                .arena(sportEventDto.getArena())
                .build();
    }

    /**
     * Converts an {@link SportEvent} entity to an {@link SportEventDto}.
     *
     * @param sportEvent the entity to convert
     * @return the converted {@link SportEventDto}
     */
    @Override
    public SportEventDto toDto(SportEvent sportEvent) {
        return SportEventDto.builder()
                .eventName(sportEvent.getEventName())
                .eventDateTime(sportEvent.getEventDateTime())
                .arena(sportEvent.getArena())
                .build();
    }
}