package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.entity.Sector;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SectorCreateEditMapper  {
    SectorCreateEditMapper INSTANCE = Mappers.getMapper(SectorCreateEditMapper.class);

    Sector toEntity(SectorCreateEditDto sectorCreateEditDto);

    SectorCreateEditDto toDto(Sector sector);
}
