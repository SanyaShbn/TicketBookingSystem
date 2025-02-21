package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaFilter;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.service.ArenaService;
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
 * REST Controller class for managing arenas in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/admin/arenas")
@RequiredArgsConstructor
@Slf4j
public class ArenaController {

    private final ArenaService arenaService;

    /**
     * Handles GET requests to retrieve and return a paginated list of arenas.
     *
     * @param arenaFilter The filter criteria for arenas.
     * @param pageable    The pagination information.
     * @return A PageResponse containing a paginated list of ArenaReadDto.
     */
    @GetMapping
    public PageResponse<ArenaReadDto> findAll(ArenaFilter arenaFilter, Pageable pageable){
        Page<ArenaReadDto> page = arenaService.findAll(arenaFilter, pageable);
        return  PageResponse.of(page);
    }

    /**
     * Handles GET requests to retrieve and return an arena by its ID.
     *
     * @param id The ID of the arena to be retrieved.
     * @return A ResponseEntity containing the ArenaReadDto if found, or a 404 Not Found status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArenaReadDto> getArenaById(@PathVariable Long id) {
        Optional<ArenaReadDto> arena = arenaService.findById(id);
        return arena.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Handles POST requests to create a new arena.
     *
     * @param arenaCreateEditDto The arena data transfer object.
     * @return A ResponseEntity containing the created ArenaReadDto and HTTP status.
     */
    @PostMapping
    public ResponseEntity<ArenaReadDto> createArena(@RequestBody @Validated ArenaCreateEditDto arenaCreateEditDto) {
        log.info("Creating new arena with details: {}", arenaCreateEditDto);
        ArenaReadDto createdArena = arenaService.createArena(arenaCreateEditDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArena);
    }

    /**
     * Handles POST requests to update an existing arena.
     *
     * @param id The ID of the arena to be updated.
     * @param arenaCreateEditDto The arena data transfer object.
     * @return A ResponseEntity containing the updated ArenaReadDto and HTTP status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArenaReadDto> updateArena(@PathVariable("id") Long id,
                                                    @RequestBody @Validated ArenaCreateEditDto arenaCreateEditDto) {
        log.info("Updating arena {} with details: {}", id, arenaCreateEditDto);
        ArenaReadDto updatedArena = arenaService.updateArena(id, arenaCreateEditDto);
        return ResponseEntity.ok(updatedArena);
    }

    /**
     * Handles POST requests to delete an existing arena.
     *
     * @param id The ID of the arena to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArena(@PathVariable("id") Long id) {
        log.info("Deleting arena with id: {}", id);
        arenaService.deleteArena(id);
        return ResponseEntity.ok("Arena deleted successfully");
    }
}