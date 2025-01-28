package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s JOIN FETCH s.row r JOIN FETCH r.sector sec " +
            "LEFT JOIN Ticket t ON s.id = t.seat.id " +
            "WHERE t.id IS NULL AND s.row.sector.arena.id IN " +
            "(SELECT se.arena.id FROM SportEvent se WHERE se.id = :eventId)")
    List<Seat> findByEventIdWithNoTickets(Long eventId);

    @Query("SELECT s FROM Seat s JOIN FETCH s.row r JOIN FETCH r.sector sec " +
            "LEFT JOIN Ticket t ON s.id = t.seat.id " +
            "WHERE (t.id IS NULL OR s.id = :seatId) " +
            "AND s.row.sector.arena.id IN " +
            "(SELECT se.arena.id FROM SportEvent se WHERE se.id = :eventId)")
    List<Seat> findAllByEventIdWhenUpdate(Long eventId, Long seatId);
}
