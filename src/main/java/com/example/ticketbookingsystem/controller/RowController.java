package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.dto.row_dto.RowFilter;
import com.example.ticketbookingsystem.dto.row_dto.RowReadDto;
import com.example.ticketbookingsystem.service.RowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller class for managing rows in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/v1/admin/rows")
@RequiredArgsConstructor
@Slf4j
public class RowController {

    private final RowService rowService;

    /**
     * Handles GET requests to retrieve and return a paginated list of rows.
     *
     * @param sectorId The ID of the sector to which the rows belong.
     * @param rowFilter The filter criteria for rows.
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of RowReadDto.
     */
    @GetMapping
    public PageResponse<RowReadDto> findAllRows(@RequestParam("sectorId") Long sectorId,
                                                                RowFilter rowFilter,
                                                                Pageable pageable) {
        Page<RowReadDto> rowsPage = rowService.findAll(sectorId, rowFilter, pageable);
        return PageResponse.of(rowsPage);
    }

    /**
     * Handles GET requests to retrieve and return a row by its ID.
     *
     * @param id The ID of the row to be retrieved.
     * @return A ResponseEntity containing the RowReadDto if found, or a 404 Not Found status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RowReadDto> getRowById(@PathVariable Long id) {
        Optional<RowReadDto> row = rowService.findById(id);
        return row.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Handles POST requests to create a new row.
     *
     * @param sectorId The ID of the sector to which the row belongs.
     * @param rowCreateEditDto The row data transfer object.
     * @return A ResponseEntity containing the created RowReadDto and HTTP status.
     */
    @PostMapping
    public ResponseEntity<RowReadDto> createRow(@RequestParam("sectorId") Long sectorId,
                                                @RequestBody @Validated RowCreateEditDto rowCreateEditDto) {
        log.info("Creating new row with details: {}", rowCreateEditDto);
        RowReadDto createdRow = rowService.createRow(rowCreateEditDto, sectorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRow);
    }

    /**
     * Handles PUT requests to update an existing row.
     *
     * @param sectorId The ID of the sector to which the row belongs.
     * @param id The ID of the row to be updated.
     * @param rowCreateEditDto The row data transfer object.
     * @return A ResponseEntity containing the updated RowReadDto and HTTP status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RowReadDto> updateRow(@RequestParam("sectorId") Long sectorId,
                                                @PathVariable("id") Long id,
                                                @RequestBody @Validated RowCreateEditDto rowCreateEditDto) {
        log.info("Updating row {} with details: {}", id, rowCreateEditDto);
        RowReadDto updatedRow = rowService.updateRow(id, rowCreateEditDto, sectorId);
        return ResponseEntity.ok(updatedRow);
    }

    /**
     * Handles DELETE requests to delete an existing row.
     *
     * @param id The ID of the row to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRow(@PathVariable("id") Long id) {
        log.info("Deleting row with id: {}", id);
        rowService.deleteRow(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Row deleted successfully");
        return ResponseEntity.ok(response);
    }
}
