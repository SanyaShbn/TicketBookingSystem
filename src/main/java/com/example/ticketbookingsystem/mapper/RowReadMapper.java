package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.RowCreateEditDto;
import com.example.ticketbookingsystem.dto.RowReadDto;
import com.example.ticketbookingsystem.entity.Row;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RowReadMapper {
    RowReadMapper INSTANCE = Mappers.getMapper(RowReadMapper.class);

    RowReadDto toDto(Row row);

    Row toEntity(RowCreateEditDto rowCreateEditDto);
}