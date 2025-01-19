package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.SeatDao;
import com.example.ticketbookingsystem.entity.Seat;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing arena's seats.
 */
public class SeatService {
    private static final SeatService INSTANCE = new SeatService();
    private final SeatDao seatDao = SeatDao.getInstance();
    private SeatService(){}

    public static SeatService getInstance(){
        return INSTANCE;
    }

    /**
     * Finds all seats.
     *
     * @return a list of all seats
     */
    public List<Seat> findAll(){
        return seatDao.findAll();
    }

    /**
     * Finds all seats by a specific event ID.
     *
     * @param eventId the ID of the event
     * @return a list of seats for a specific event
     */
    public List<Seat> findAllByEventId(Long eventId){
        return seatDao.findAllByEventId(eventId);
    }

    /**
     * Finds seats to add seat tickets by a specific event ID.
     *
     * @param eventId the ID of the event
     * @return a list of seats which are not yet available for ticket purchasing
     */
    public List<Seat> findByEventIdWithNoTickets(Long eventId){
        return seatDao.findByEventIdWithNoTickets(eventId);
    }

    /**
     * Finds seats to add seat tickets by a specific event ID including the updating one.
     *
     * @param eventId the ID of the event
     * @return a list of seats which are not yet available for ticket purchasing and the updating one
     */
    public List<Seat> findAllByEventIdWhenUpdate(Long eventId, Long seatId){
        return seatDao.findAllByEventIdWhenUpdate(eventId, seatId);
    }

    /**
     * Finds a seat by its ID.
     *
     * @param id the ID of the seat
     * @return an {@link Optional} containing the found seat, or empty if not found
     */
    public Optional<Seat> findById(Long id){
        return seatDao.findById(id);
    }

}
