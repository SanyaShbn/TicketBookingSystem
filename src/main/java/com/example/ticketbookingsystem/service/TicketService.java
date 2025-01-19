package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.TicketDao;
import com.example.ticketbookingsystem.dto.TicketDto;
import com.example.ticketbookingsystem.dto.TicketFilter;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.mapper.TicketMapper;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing tickets to sporting events.
 */
public class TicketService {
    private static final TicketService INSTANCE = new TicketService();
    private final TicketDao ticketDao = TicketDao.getInstance();
    private final TicketMapper ticketMapper = TicketMapper.getInstance();
    private TicketService(){}

    public static TicketService getInstance(){
        return INSTANCE;
    }

    /**
     * Finds all tickets.
     *
     * @return a list of all tickets
     */
    public List<Ticket> findAll(){
        return ticketDao.findAll();
    }

    /**
     * Finds all tickets matching the given filter.
     *
     * @param ticketFilter the filter to apply
     * @param eventId the ID of the event for which is needed to get tickets
     * @return a list of tickets matching the filter
     */
    public List<Ticket> findAll(TicketFilter ticketFilter, Long eventId){
        return ticketDao.findAll(ticketFilter, eventId);
    }

    /**
     * Finds a list of tickets to a specific event.
     *
     * @param eventId the ID of the sporting event
     * @return a list of tickets matching the event
     */
    public List<Ticket> findAllByEventId(Long eventId){
        return ticketDao.findAllByEventId(eventId);
    }

    /**
     * Finds a ticket by its ID.
     *
     * @param id the ID of the ticket
     * @return an {@link Optional} containing the found ticket, or empty if not found
     */
    public Optional<Ticket> findById(Long id){
        return ticketDao.findById(id);
    }

    /**
     * Creates a new ticket.
     *
     * @param ticketDto the DTO of the ticket to create
     */
    public void createTicket(TicketDto ticketDto) {
        Ticket ticket = ticketMapper.toEntity(ticketDto);
        ticketDao.save(ticket);
    }

    /**
     * Updates an existing ticket.
     *
     * @param id the ID of the ticket to update
     * @param ticketDto the DTO of the updated ticket
     */
    public void updateTicket(Long id, TicketDto ticketDto) {
        Ticket ticket = ticketMapper.toEntity(ticketDto);
        ticket.setId(id);
        ticketDao.update(ticket);
    }

    /**
     * Retrieves the status of a specific ticket.
     *
     * @param ticketId the ID of the ticket to get status
     * @return retrieved ticket status as a String
     */
    public String getTicketStatus(Long ticketId) {
        return ticketDao.getTicketStatus(ticketId);
    }

    /**
     * Deletes a ticket by its ID.
     *
     * @param id the ID of the ticket to delete
     */
    public void deleteTicket(Long id) {
        ticketDao.delete(id);
    }

}
