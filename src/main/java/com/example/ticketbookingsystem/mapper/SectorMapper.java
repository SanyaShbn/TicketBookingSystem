package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.SectorDto;
import com.example.ticketbookingsystem.entity.Sector;

/**
 * Mapper class for converting between {@link Sector} entity and {@link SectorDto} DTO.
 */
public class SectorMapper implements Mapper<Sector, SectorDto> {

    private static final SectorMapper INSTANCE = new SectorMapper();

    private SectorMapper(){}
    public static SectorMapper getInstance(){
        return INSTANCE;
    }

    /**
     * Converts an {@link SectorDto} to an {@link Sector} entity.
     *
     * @param sectorDto the DTO to convert
     * @return the converted {@link Sector} entity
     */
    @Override
    public Sector toEntity(SectorDto sectorDto) {
        return Sector.builder()
                .sectorName(sectorDto.getSectorName())
                .arena(sectorDto.getArena())
                .maxRowsNumb(sectorDto.getMaxRowsNumb())
                .maxSeatsNumb(sectorDto.getMaxSeatsNumb())
                .build();
    }

    /**
     * Converts an {@link Sector} entity to an {@link SectorDto}.
     *
     * @param sector the entity to convert
     * @return the converted {@link SectorDto}
     */
    @Override
    public SectorDto toDto(Sector sector) {
        return SectorDto.builder()
                .sectorName(sector.getSectorName())
                .arena(sector.getArena())
                .maxRowsNumb(sector.getMaxRowsNumb())
                .maxSeatsNumb(sector.getMaxSeatsNumb())
                .build();
    }
}
