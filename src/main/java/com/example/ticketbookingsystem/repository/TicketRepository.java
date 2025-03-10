package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Ticket entities.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t " +
            "JOIN FETCH t.sportEvent se " +
            "JOIN FETCH t.seat s " +
            "JOIN FETCH s.row r " +
            "JOIN FETCH r.sector sec " +
            "JOIN FETCH sec.arena WHERE t.id = :id")
    Optional<Ticket> findById(@Param("id") Long id);

    @Query("SELECT t.status FROM Ticket t WHERE t.id = :ticketId")
    Optional<String> findStatusById(Long ticketId);

    @Query("FROM Ticket t JOIN FETCH t.sportEvent se JOIN FETCH t.seat s JOIN FETCH s.row r " +
            "JOIN FETCH r.sector sec JOIN FETCH sec.arena a WHERE t.sportEvent.id = :eventId")
    Page<Ticket> findAllBySportEventIdPageable(Long eventId, Pageable sortedPageable);

    @Query("FROM Ticket t JOIN FETCH t.sportEvent se JOIN FETCH t.seat s JOIN FETCH s.row r " +
            "JOIN FETCH r.sector sec JOIN FETCH sec.arena a WHERE t.sportEvent.id = :eventId")
    List<Ticket> findAllBySportEventId(Long eventId);

}