package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.service.ImageService;
import com.example.ticketbookingsystem.service.SportEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller class for managing sport events in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/v1/admin/sport_events")
@RequiredArgsConstructor
@Slf4j
public class SportEventController {

    private final SportEventService sportEventService;

    private final ImageService imageService;

    /**
     * Handles GET requests to retrieve and return a paginated list of sport events.
     *
     * @param sportEventFilter The filter criteria for sport events.
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of SportEventReadDto.
     */
    @GetMapping
    public PageResponse<SportEventReadDto> findAllSportEvents(SportEventFilter sportEventFilter, Pageable pageable) {
        Page<SportEventReadDto> sportEventsPage = sportEventService.findAll(sportEventFilter, pageable);
        return PageResponse.of(sportEventsPage);
    }

    /**
     * Handles GET requests to retrieve and return a sport event by its ID.
     *
     * @param id The ID of the sport event to be retrieved.
     * @return A ResponseEntity containing the SportEventReadDto if found, or a 404 Not Found status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SportEventReadDto> getSportEventById(@PathVariable Long id) {
        Optional<SportEventReadDto> sportEvent = sportEventService.findById(id);
        return sportEvent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Handles POST requests to create a new sport event.
     *
     * @param arenaId The ID of the arena to which the sport event belongs.
     * @param sportEventCreateEditDto The sport event data transfer object.
     * @return A ResponseEntity containing the created SportEventReadDto and HTTP status.
     */
    @PostMapping
    public ResponseEntity<SportEventReadDto> createSportEvent(@RequestParam("arenaId") Long arenaId,
                                                              @ModelAttribute @Validated SportEventCreateEditDto sportEventCreateEditDto) {
        log.info("Creating new sporting event with details: {}", sportEventCreateEditDto);

        SportEventReadDto createdSportEvent = sportEventService
                .createSportEvent(sportEventCreateEditDto, arenaId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSportEvent);
    }

    /**
     * Handles PUT requests to update an existing sport event.
     *
     * @param arenaId The ID of the arena to which the sport event belongs.
     * @param id The ID of the sport event to be updated.
     * @param sportEventCreateEditDto The sport event data transfer object.
     * @return A ResponseEntity containing the updated SportEventReadDto and HTTP status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SportEventReadDto> updateSportEvent(@RequestParam("arenaId") Long arenaId,
                                                              @PathVariable("id") Long id,
                                                              @ModelAttribute @Validated SportEventCreateEditDto sportEventCreateEditDto) {
        log.info("Updating sporting event {} with details: {}", id, sportEventCreateEditDto);

        SportEventReadDto updatedSportEvent = sportEventService
                .updateSportEvent(id, sportEventCreateEditDto, arenaId);

        return ResponseEntity.ok(updatedSportEvent);
    }

    /**
     * Handles DELETE requests to delete an existing sport event.
     *
     * @param id The ID of the sport event to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSportEvent(@PathVariable("id") Long id) {
        log.info("Deleting sporting event with id: {}", id);
        sportEventService.deleteSportEvent(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Sporting event deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a poster image of the sport event by its filename.
     *
     * @param filename the name of the poster image file
     * @return a ResponseEntity containing the image bytes, or a 404 status if not found
     */
    @GetMapping(value = "/poster/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> findPosterImage(@PathVariable("filename") String filename) {
        byte[] imageBytes = imageService.fetchImage(filename);
        if (imageBytes != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .contentLength(imageBytes.length)
                    .body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a poster image by its filename.
     *
     * @param filename the name of the poster image file
     * @return a ResponseEntity with a 204 status if the image was deleted successfully
     */
    @DeleteMapping("/poster/{filename}")
    public ResponseEntity<Void> deletePosterImage(@PathVariable("filename") String filename) {
        sportEventService.deletePosterImageForSportEvent(filename);
        return ResponseEntity.noContent().build();
    }

}
