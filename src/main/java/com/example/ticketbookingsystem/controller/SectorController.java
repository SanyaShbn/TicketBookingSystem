package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.sector_dto.SectorFilter;
import com.example.ticketbookingsystem.dto.sector_dto.SectorReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.service.SectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Controller class for managing sectors in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/admin/sectors")
@RequiredArgsConstructor
@Slf4j
public class SectorController {

    private final SectorService sectorService;

    private final MessageSource messageSource;

    /**
     * Handles GET requests to retrieve and display all sectors.
     *
     * @param arenaId The ID of the arena to which the sectors belong.
     * @param sectorFilter The filter criteria for sectors.
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of SectorReadDto.
     */
    @GetMapping
    public PageResponse<SectorReadDto> findAllSectors(@RequestParam("arenaId") Long arenaId,
                                                                      SectorFilter sectorFilter,
                                                                      Pageable pageable) {
        Page<SectorReadDto> sectorsPage = sectorService.findAll(arenaId, sectorFilter, pageable);
        return PageResponse.of(sectorsPage);
    }

    /**
     * Handles GET requests to retrieve and return a sector by its ID.
     *
     * @param id The ID of the sector to be retrieved.
     * @return A ResponseEntity containing the SectorReadDto if found, or a 404 Not Found status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SectorReadDto> getSectorById(@PathVariable Long id) {
        Optional<SectorReadDto> sector = sectorService.findById(id);
        return sector.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Handles POST requests to create a new sector.
     *
     * @param arenaId The ID of the arena to which the sector belongs.
     * @param sectorCreateEditDto The sector data transfer object.
     * @return A ResponseEntity containing the created SectorReadDto and HTTP status.
     */
    @PostMapping
    public ResponseEntity<SectorReadDto> createSector(@RequestParam("arenaId") Long arenaId,
                                                      @RequestBody @Validated SectorCreateEditDto sectorCreateEditDto) {
        try {
            log.info("Creating new sector with details: {}", sectorCreateEditDto);
            SectorReadDto createdSector = sectorService.createSector(sectorCreateEditDto, arenaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSector);
        } catch (DaoCrudException | DaoResourceNotFoundException e) {
            log.error("CRUD exception occurred while creating sector: {}", e.getMessage());
            SectorReadDto failedToCreateSector = createFailedSectorReadDto(sectorCreateEditDto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedToCreateSector);
        }
    }

    /**
     * Handles PUT requests to update an existing sector.
     *
     * @param arenaId The ID of the arena to which the sector belongs.
     * @param id The ID of the sector to be updated.
     * @param sectorCreateEditDto The sector data transfer object.
     * @return A ResponseEntity containing the updated SectorReadDto and HTTP status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SectorReadDto> updateSector(@RequestParam("arenaId") Long arenaId,
                               @PathVariable("id") Long id,
                               @RequestBody @Validated SectorCreateEditDto sectorCreateEditDto) {
        try {
            log.info("Updating sector {} with details: {}", id, sectorCreateEditDto);
            SectorReadDto updatedSector = sectorService.updateSector(id, sectorCreateEditDto, arenaId);
            return ResponseEntity.ok(updatedSector);
        } catch (DaoCrudException | DaoResourceNotFoundException e) {
            log.error("CRUD exception occurred while updating sector: {}", e.getMessage());
            SectorReadDto failedToUpdateSector = createFailedSectorReadDto(sectorCreateEditDto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedToUpdateSector);
        }
    }

    /**
     * Handles DELETE requests to delete an existing sector.
     *
     * @param id The ID of the sector to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSector(@PathVariable("id") Long id) {
        try {
            log.info("Deleting sector with id: {}", id);
            sectorService.deleteSector(id);
            return ResponseEntity.ok("Sector deleted successfully");
        } catch (DaoCrudException | DaoResourceNotFoundException e) {
            log.error("CRUD exception occurred while trying to delete sector: {}", e.getMessage());
            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("delete.sector.fail", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    private SectorReadDto createFailedSectorReadDto(SectorCreateEditDto sectorCreateEditDto) {
        return SectorReadDto.builder()
                .sectorName(sectorCreateEditDto.getSectorName())
                .maxRowsNumb(sectorCreateEditDto.getMaxRowsNumb())
                .maxSeatsNumb(sectorCreateEditDto.getMaxSeatsNumb())
                .build();
    }

}
