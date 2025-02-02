package com.example.ticketbookingsystem.dto.row_dto;

import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RowCreateEditDto {

    @NotNull(message = "{error.not.null}")
    @Min(value = 1, message = "{error.min.row.number}")
    Integer rowNumber;

    @NotNull(message = "{error.not.null}")
    @Min(value = 1, message = "{error.min.seats.numb}")
    Integer seatsNumb;

    Sector sector;
}
