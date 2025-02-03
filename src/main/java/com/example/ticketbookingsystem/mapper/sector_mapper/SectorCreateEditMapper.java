package com.example.ticketbookingsystem.mapper.sector_mapper;

import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.entity.Sector;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Sector entities and SectorCreateEditDto.
 */
@Mapper(componentModel = "spring")
public interface SectorCreateEditMapper  {
    SectorCreateEditMapper INSTANCE = Mappers.getMapper(SectorCreateEditMapper.class);

    Sector toEntity(SectorCreateEditDto sectorCreateEditDto);

    SectorCreateEditDto toDto(Sector sector);
}
