package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.sector_dto.SectorFilter;
import com.example.ticketbookingsystem.dto.sector_dto.SectorReadDto;
import com.example.ticketbookingsystem.service.SectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST Controller class for managing sectors in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/admin/sectors")
@RequiredArgsConstructor
@Slf4j
public class SectorController {

    private final SectorService sectorService;

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
        log.info("Creating new sector with details: {}", sectorCreateEditDto);
        SectorReadDto createdSector = sectorService.createSector(sectorCreateEditDto, arenaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSector);
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
        log.info("Updating sector {} with details: {}", id, sectorCreateEditDto);
        SectorReadDto updatedSector = sectorService.updateSector(id, sectorCreateEditDto, arenaId);
        return ResponseEntity.ok(updatedSector);
    }

    /**
     * Handles DELETE requests to delete an existing sector.
     *
     * @param id The ID of the sector to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSector(@PathVariable("id") Long id) {
        log.info("Deleting sector with id: {}", id);
        sectorService.deleteSector(id);
        return ResponseEntity.ok("Sector deleted successfully");
    }

}
