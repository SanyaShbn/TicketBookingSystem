package com.example.ticketbookingsystem.dto.sector_dto;

import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import com.example.ticketbookingsystem.validator.custom_annotations.ValidSectorName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SectorCreateEditDto {

    @LocalizedNotBlank
    @ValidSectorName
    String sectorName;

    @NotNull(message = "{error.not.null}")
    @Min(value = 1, message = "{error.max.rows}")
    Integer maxRowsNumb;

    @NotNull(message = "{error.not.null}")
    @Min(value = 1, message = "{error.max.seats}")
    Integer maxSeatsNumb;
}