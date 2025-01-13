package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.TicketDao;
import com.example.ticketbookingsystem.dto.TicketDto;
import com.example.ticketbookingsystem.dto.TicketFilter;
import com.example.ticketbookingsystem.entity.Ticket;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class TicketService {
    private static final TicketService INSTANCE = new TicketService();
    private final TicketDao ticketDao = TicketDao.getInstance();
    private TicketService(){}

    public static TicketService getInstance(){
        return INSTANCE;
    }

    public List<Ticket> findAll(){
        return ticketDao.findAll();
    }

    public List<Ticket> findAll(TicketFilter ticketFilter, Long eventId){
        return ticketDao.findAll(ticketFilter, eventId);
    }

    public List<Ticket> findAllByEventId(Long eventId){
        return ticketDao.findAllByEventId(eventId);
    }

    public Optional<Ticket> findById(Long id){
        return ticketDao.findById(id);
    }
    public void createTicket(TicketDto ticketDto) {
        Ticket ticket = buildTicketFromDto(ticketDto);
        ticketDao.save(ticket);
    }

    public void updateTicket(Long id, TicketDto ticketDto) {
        Ticket ticket = buildTicketFromDto(ticketDto);
        ticket.setId(id);
        ticketDao.update(ticket);
    }

    public String getTicketStatus(Long ticketId) {
        return ticketDao.getTicketStatus(ticketId);
    }

    public void deleteTicket(Long id) {
        ticketDao.delete(id);
    }

    private Ticket buildTicketFromDto(TicketDto ticketDto) {
        return Ticket.builder()
                .status(ticketDto.getStatus())
                .price(ticketDto.getPrice())
                .sportEvent(ticketDto.getSportEvent())
                .seat(ticketDto.getSeat())
                .build();
    }

}
