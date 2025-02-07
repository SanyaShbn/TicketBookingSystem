package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.service.SportEventService;
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
 * REST Controller class for managing sport events in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/admin/sport_events")
@RequiredArgsConstructor
@Slf4j
public class SportEventController {

    private final SportEventService sportEventService;

    private final MessageSource messageSource;

    /**
     * Handles GET requests to retrieve and return a paginated list of sport events.
     *
     * @param sportEventFilter The filter criteria for sport events.
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of SportEventReadDto.
     */
    @GetMapping
    public PageResponse<SportEventReadDto> findAllSportEvents(SportEventFilter sportEventFilter,
                                                                              Pageable pageable) {
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
        try {
            log.info("Creating new sporting event with details: {}", sportEventCreateEditDto);
            SportEventReadDto createdSportEvent = sportEventService.createSportEvent(sportEventCreateEditDto, arenaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSportEvent);
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating sporting event: {}", e.getMessage());
            SportEventReadDto failedToCreateSportEvent = createFailedSportEventReadDto(sportEventCreateEditDto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedToCreateSportEvent);
        }
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
        try {
            log.info("Updating sporting event {} with details: {}", id, sportEventCreateEditDto);
            SportEventReadDto updatedSportEvent = sportEventService.updateSportEvent(
                    id,
                    sportEventCreateEditDto,
                    arenaId
            );
            return ResponseEntity.ok(updatedSportEvent);
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating sporting event: {}", e.getMessage());
            SportEventReadDto failedToUpdateSportEvent = createFailedSportEventReadDto(sportEventCreateEditDto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedToUpdateSportEvent);
        }
    }

    /**
     * Handles DELETE requests to delete an existing sport event.
     *
     * @param id The ID of the sport event to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSportEvent(@PathVariable("id") Long id) {
        try {
            log.info("Deleting sporting event with id: {}", id);
            sportEventService.deleteSportEvent(id);
            return ResponseEntity.ok("Sporting event deleted successfully");
        } catch (DaoCrudException | DaoResourceNotFoundException e) {
            log.error("CRUD exception occurred while trying to delete sporting event: {}", e.getMessage());
            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("delete.sport.event.fail", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    private SportEventReadDto createFailedSportEventReadDto(SportEventCreateEditDto sportEventCreateEditDto) {
        return SportEventReadDto.builder()
                .eventName(sportEventCreateEditDto.getEventName())
                .eventDateTime(sportEventCreateEditDto.getEventDateTime())
                .build();
    }

}
