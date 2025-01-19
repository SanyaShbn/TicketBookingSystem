package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.RowDto;
import com.example.ticketbookingsystem.entity.Row;

/**
 * Mapper class for converting between {@link Row} entity and {@link RowDto} DTO.
 */
public class RowMapper implements Mapper<Row, RowDto> {

    private static final RowMapper INSTANCE = new RowMapper();

    private RowMapper(){}
    public static RowMapper getInstance(){
        return INSTANCE;
    }

    /**
     * Converts an {@link RowDto} to an {@link Row} entity.
     *
     * @param rowDto the DTO to convert
     * @return the converted {@link Row} entity
     */
    @Override
    public Row toEntity(RowDto rowDto) {
        return Row.builder()
                .rowNumber(rowDto.getRowNumber())
                .seatsNumb(rowDto.getSeatsNumb())
                .sector(rowDto.getSector())
                .build();
    }

    /**
     * Converts an {@link Row} entity to an {@link RowDto}.
     *
     * @param row the entity to convert
     * @return the converted {@link RowDto}
     */
    @Override
    public RowDto toDto(Row row) {
        return RowDto.builder()
                .rowNumber(row.getRowNumber())
                .seatsNumb(row.getSeatsNumb())
                .sector(row.getSector())
                .build();
    }
}
