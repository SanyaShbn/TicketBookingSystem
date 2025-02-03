package com.example.ticketbookingsystem.dto.sector_dto;

import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import com.example.ticketbookingsystem.validator.custom_annotations.ValidSectorName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * SectorCreateEditDto - Data Transfer Object representing information about a sector
 * when creating or updating sector.
 */
@Value
@Builder
public class SectorCreateEditDto {

    @LocalizedNotBlank
    @ValidSectorName
    String sectorName;

    @NotNull
    @Min(value = 1)
    Integer maxRowsNumb;

    @NotNull
    @Min(value = 1)
    Integer maxSeatsNumb;
}