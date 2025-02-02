package com.example.ticketbookingsystem.mapper.row_mapper;

import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.entity.Row;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Row entities and RowCreateEditDto.
 */
@Mapper(componentModel = "spring")
public interface RowCreateEditMapper {
    RowCreateEditMapper INSTANCE = Mappers.getMapper(RowCreateEditMapper.class);

    Row toEntity(RowCreateEditDto rowCreateEditDto);

    RowCreateEditDto toDto(Row row);
}
