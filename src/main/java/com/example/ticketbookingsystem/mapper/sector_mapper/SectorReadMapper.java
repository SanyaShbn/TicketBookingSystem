package com.example.ticketbookingsystem.mapper.sector_mapper;

import com.example.ticketbookingsystem.dto.sector_dto.SectorReadDto;
import com.example.ticketbookingsystem.entity.Sector;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SectorReadMapper {
    SectorReadMapper INSTANCE = Mappers.getMapper(SectorReadMapper.class);

    SectorReadDto toDto(Sector sector);

    Sector toEntity(SectorReadDto sectorReadDto);
}
