package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.RowCreateEditDto;
import com.example.ticketbookingsystem.entity.Row;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RowCreateEditMapper {
    RowCreateEditMapper INSTANCE = Mappers.getMapper(RowCreateEditMapper.class);

    Row toEntity(RowCreateEditDto rowCreateEditDto);
    RowCreateEditDto toDto(Row row);
}
