package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
import com.example.ticketbookingsystem.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller class for managing seats in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/v1/admin/seats")
@RequiredArgsConstructor
@Slf4j
public class SeatController {

    private final SeatService seatService;

    /**
     * Handles GET requests to retrieve and display available seats when choosing a ticket to purchase (by users).
     *
     * @param eventId The ID of the event which the seats belong to.
     * @return list of all seats for this sporting event (mapped into SeatReadDto).
     */
    @GetMapping
    public ResponseEntity<List<SeatReadDto>> findByEventId(@RequestParam("eventId") Long eventId) {
        List<SeatReadDto> seats = seatService.findByEventId(eventId);
        return ResponseEntity.ok(seats);
    }

    /**
     * Handles GET requests to retrieve and display available seats when creating new ticket.
     *
     * @param eventId The ID of the event which the seats belong to.
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of SeatReadDto.
     */
    @GetMapping("/when_ticket_create")
    public PageResponse<SeatReadDto> findAvailableSeatsWhenAddingNewTicket(@RequestParam("eventId") Long eventId,
                                                                           Pageable pageable) {
        Page<SeatReadDto> seatsPage = seatService.findByEventIdWithNoTickets(eventId, pageable);
        return PageResponse.of(seatsPage);
    }

    /**
     * Handles GET requests to retrieve and display available seats when updating a certain ticket.
     *
     * @param eventId The ID of the event which the seats belong to.
     * @param seatId the ID of the seat for which there is already a ticket (the updating one)
     * @param pageable The pagination information.
     * @return A PageResponse containing a paginated list of SeatReadDto.
     */
    @GetMapping("/when_ticket_update")
    public PageResponse<SeatReadDto> findAvailableSeatsWhenUpdatingExistingTicket(@RequestParam("eventId") Long eventId,
                                                                                  @RequestParam("seatId") Long seatId,
                                                                                  Pageable pageable) {
        Page<SeatReadDto> seatsPage = seatService.findAllByEventIdWhenUpdate(eventId, seatId, pageable);
        return PageResponse.of(seatsPage);
    }
}
