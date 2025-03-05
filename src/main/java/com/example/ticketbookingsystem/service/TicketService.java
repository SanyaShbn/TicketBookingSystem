package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketFilter;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.seat_mapper.SeatReadMapper;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.mapper.ticket_mapper.TicketCreateEditMapper;
import com.example.ticketbookingsystem.mapper.ticket_mapper.TicketReadMapper;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.utils.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing tickets to sporting events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private static final String SORT_BY_PRICE = "price";

    private final TicketRepository ticketRepository;

    private final TicketReadMapper ticketReadMapper;

    private final TicketCreateEditMapper ticketCreateEditMapper;

    private final SportEventService sportEventService;

    private final SportEventReadMapper sportEventReadMapper;

    private final SeatService seatService;

    private final SeatReadMapper seatReadMapper;

    /**
     * Finds all tickets.
     *
     * @return a list of all tickets
     */
    public List<TicketReadDto> findAll(){
        return ticketRepository.findAll().stream()
                .map(ticketReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds all tickets matching the given filter.
     *
     * @param ticketFilter the filter to apply
     * @param eventId the ID of the event for which is needed to get tickets
     * @param pageable object of Pageable interface to apply pagination correctly
     * @return a list of tickets matching the filter
     */
    public Page<TicketReadDto> findAll(Long eventId, TicketFilter ticketFilter, Pageable pageable){
        Map<String, String> sortOrders = new LinkedHashMap<>();
        if (ticketFilter.priceSortOrder() != null && !ticketFilter.priceSortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_PRICE, ticketFilter.priceSortOrder());
        }

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return ticketRepository.findAllBySportEventId(eventId, sortedPageable)
                .map(ticketReadMapper::toDto);
    }

    /**
     * Finds a ticket by its ID.
     *
     * @param id the ID of the ticket
     * @return an {@link Optional} containing the found ticket, or empty if not found
     */
    public Optional<TicketReadDto> findById(Long id){
        return ticketRepository.findById(id)
                .map(ticketReadMapper::toDto);
    }

    /**
     * Creates a new ticket.
     *
     * @param ticketCreateEditDto the DTO of the ticket to create
     * @param eventId the ID of the sporting event to save ticket for
     * @param seatId the ID of the seat to save ticket for
     */
    @Transactional
    public TicketReadDto createTicket(TicketCreateEditDto ticketCreateEditDto, Long eventId, Long seatId) {
        try {
            Ticket ticket = ticketCreateEditMapper.toEntity(ticketCreateEditDto);
            SportEvent sportEvent = getSportEventById(eventId);
            Seat seat = getSeatById(seatId);

            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setSportEvent(sportEvent);
            ticket.setSeat(seat);

            Ticket savedTicket = ticketRepository.save(ticket);
            log.info("Ticket created successfully with dto: {}", ticketCreateEditDto);
            return ticketReadMapper.toDto(savedTicket);
        } catch (DataAccessException e){
            log.error("Failed to create Ticket with dto: {}", ticketCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Updates an existing ticket.
     *
     * @param id the ID of the ticket to update
     * @param ticketCreateEditDto the DTO of the ticket to create
     * @param eventId the ID of the sporting event to save ticket for
     * @param seatId the ID of the seat to save ticket for
     */
    @Transactional
    public TicketReadDto updateTicket(Long id, TicketCreateEditDto ticketCreateEditDto, Long eventId, Long seatId) {
        try {
            Ticket ticket = ticketCreateEditMapper.toEntity(ticketCreateEditDto);
            SportEvent sportEvent = getSportEventById(eventId);
            Seat seat = getSeatById(seatId);

            ticket.setId(id);
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setSportEvent(sportEvent);
            ticket.setSeat(seat);

            Ticket updatedTicket = ticketRepository.save(ticket);
            log.info("Ticket updated successfully with dto: {}", ticketCreateEditDto);
            return ticketReadMapper.toDto(updatedTicket);
        } catch (DataAccessException e){
            log.error("Failed to update Ticket {} with dto: {}", id, ticketCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Retrieves the status of a specific ticket.
     *
     * @param ticketId the ID of the ticket to get status
     * @return retrieved ticket status as a String
     */
    public String getTicketStatus(Long ticketId) {
        return ticketRepository.findStatusById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("No status found for ticket with ID: " + ticketId));
    }

    /**
     * Deletes a ticket by its ID.
     *
     * @param id the ID of the ticket to delete
     */
    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            log.error("Failed to find ticket with provided id: {}", id);
            throw new DaoResourceNotFoundException("Ticket not found with id " + id);
        }
        try {
            ticketRepository.deleteById(id);
            log.info("Ticket with id {} deleted successfully.", id);
        } catch (DataAccessException e){
            log.error("Failed to delete Ticket with id {}", id);
            throw new DaoCrudException(e);
        }
    }

    private SportEvent getSportEventById(Long eventId) {
        SportEventReadDto sportEventReadDto = sportEventService.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Sport Event not found for id = " + eventId));
        return sportEventReadMapper.toEntity(sportEventReadDto);
    }

    private Seat getSeatById(Long seatId) {
        SeatReadDto seatReadDto = seatService.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found for id = " + seatId));
        return seatReadMapper.toEntity(seatReadDto);
    }

}
