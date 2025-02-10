package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketFilter;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.service.TicketService;
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
 * REST Controller class for managing tickets in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    private final MessageSource messageSource;

    /**
     * Handles GET requests to retrieve and display all tickets.
     *
     * @param eventId The ID of the event to which the tickets belong.
     * @param ticketFilter The filter criteria for ticekts.
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of TicketReadDto.
     */
    @GetMapping
    public PageResponse<TicketReadDto> findAllTickets(@RequestParam("eventId") Long eventId,
                                                      TicketFilter ticketFilter,
                                                      Pageable pageable) {
        Page<TicketReadDto> ticketsPage = ticketService.findAll(eventId, ticketFilter, pageable);
        return PageResponse.of(ticketsPage);
    }

    /**
     * Handles GET requests to retrieve and return a ticket by its ID.
     *
     * @param id The ID of the ticket to be retrieved.
     * @return A ResponseEntity containing the TicketReadDto if found, or a 404 Not Found status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketReadDto> getTicketById(@PathVariable Long id) {
        Optional<TicketReadDto> ticket = ticketService.findById(id);
        return ticket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Handles POST requests to create a new ticket.
     *
     * @param eventId The ID of the event to which the ticket belongs.
     * @param seatId The ID of the seat to which the ticket belongs.
     * @param ticketCreateEditDto The ticket data transfer object.
     * @return A ResponseEntity containing the created TicketReadDto and HTTP status.
     */
    @PostMapping
    public ResponseEntity<TicketReadDto> createTicket(@RequestParam("eventId") Long eventId,
                                                      @RequestParam("seatId") Long seatId,
                                                      @ModelAttribute @Validated TicketCreateEditDto ticketCreateEditDto) {
        try {
            log.info("Creating new ticket with details: {}", ticketCreateEditDto);
            TicketReadDto savedTicket = ticketService.createTicket(ticketCreateEditDto, eventId, seatId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            TicketReadDto failedToCreateTicket = createFailedTicketReadDto(ticketCreateEditDto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedToCreateTicket);
        }
    }

    /**
     * Handles PUT requests to update an existing ticket.
     *
     * @param eventId The ID of the event to which the ticket belongs.
     * @param seatId The ID of the seat to which the ticket belongs.
     * @param id The ID of the ticket to be updated.
     * @param ticketCreateEditDto The ticket data transfer object.
     * @return A ResponseEntity containing the updated TicketReadDto and HTTP status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketReadDto> updateTicket(@RequestParam("eventId") Long eventId,
                               @RequestParam("seatId") Long seatId,
                               @PathVariable("id") Long id,
                               @ModelAttribute @Validated TicketCreateEditDto ticketCreateEditDto) {
        try {
            log.info("Updating ticket {} with details: {}", id, ticketCreateEditDto);
            TicketReadDto updatedTicket = ticketService.updateTicket(id, ticketCreateEditDto, eventId, seatId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedTicket);
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            TicketReadDto failedToUpdateTicket = createFailedTicketReadDto(ticketCreateEditDto);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failedToUpdateTicket);
        }
    }

    /**
     * Handles DELETE requests to delete an existing ticket.
     *
     * @param id The ID of the ticket to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable("id") Long id) {
        try {
            log.info("Deleting ticket with id: {}", id);
            ticketService.deleteTicket(id);
            return ResponseEntity.ok("Ticket deleted successfully");
        } catch (DaoCrudException | DaoResourceNotFoundException e) {
            log.error("CRUD exception occurred while trying to delete ticket: {}", e.getMessage());
            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("delete.ticket.fail", null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    private TicketReadDto createFailedTicketReadDto(TicketCreateEditDto ticketCreateEditDto) {
        return TicketReadDto.builder()
                .price(ticketCreateEditDto.getPrice())
                .status(ticketCreateEditDto.getStatus())
                .build();
    }

}
