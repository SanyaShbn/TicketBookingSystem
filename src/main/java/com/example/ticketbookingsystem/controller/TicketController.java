package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketFilter;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller class for managing tickets in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/v1/admin/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    /**
     * Handles GET requests to retrieve and display all tickets for a specific sport event.
     *
     * @param eventId The ID of the event to which the tickets belong.
     * @return list of retrieved tickets.
     */
    @GetMapping("/all")
    public ResponseEntity<List<TicketReadDto>> findAllTicketsByEventId(@RequestParam("eventId") Long eventId) {
        List<TicketReadDto> tickets = ticketService.findAllByEventId(eventId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Handles GET requests to retrieve and display pageable tickets for a specific sport event.
     *
     * @param eventId The ID of the event to which the tickets belong.
     * @param ticketFilter The filter criteria for tickets.
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of TicketReadDto.
     */
    @GetMapping
    public PageResponse<TicketReadDto> findAllTicketsByEventId(@RequestParam("eventId") Long eventId,
                                                      TicketFilter ticketFilter,
                                                      Pageable pageable) {
        Page<TicketReadDto> ticketsPage = ticketService.findAllByEventId(eventId, ticketFilter, pageable);
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
                                                      @RequestBody @Validated TicketCreateEditDto ticketCreateEditDto) {
        log.info("Creating new ticket with details: {}", ticketCreateEditDto);
        TicketReadDto savedTicket = ticketService.createTicket(ticketCreateEditDto, eventId, seatId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
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
                               @RequestBody @Validated TicketCreateEditDto ticketCreateEditDto) {
        log.info("Updating ticket {} with details: {}", id, ticketCreateEditDto);
        TicketReadDto updatedTicket = ticketService.updateTicket(id, ticketCreateEditDto, eventId, seatId);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedTicket);
    }

    /**
     * Handles DELETE requests to delete an existing ticket.
     *
     * @param id The ID of the ticket to be deleted.
     * @return A ResponseEntity containing the HTTP status of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTicket(@PathVariable("id") Long id) {
        log.info("Deleting ticket with id: {}", id);
        ticketService.deleteTicket(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ticket deleted successfully");
        return ResponseEntity.ok(response);
    }

}
