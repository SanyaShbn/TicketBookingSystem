package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.SeatDao;
import com.example.ticketbookingsystem.entity.Seat;
import java.util.List;
import java.util.Optional;

public class SeatService {
    private final static SeatService INSTANCE = new SeatService();
    private final SeatDao seatDao = SeatDao.getInstance();
    private SeatService(){}

    public static SeatService getInstance(){
        return INSTANCE;
    }

    public List<Seat> findAll(){
        return seatDao.findAll();
    }

    public List<Seat> findAllByEventId(Long eventId){
        return seatDao.findAllByEventId(eventId);
    }
    public List<Seat> findByEventIdWithNoTickets(Long eventId){
        return seatDao.findByEventIdWithNoTickets(eventId);
    }
    public List<Seat> findAllByEventIdWhenUpdate(Long eventId, Long seatId){
        return seatDao.findAllByEventIdWhenUpdate(eventId, seatId);
    }

    public Optional<Seat> findById(Long id){
        return seatDao.findById(id);
    }

}
