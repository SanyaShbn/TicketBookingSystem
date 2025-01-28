package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<String> findStatusById(Long ticketId);

    @Query("FROM Ticket t JOIN FETCH t.sportEvent se JOIN FETCH t.seat s JOIN FETCH s.row r " +
            "JOIN FETCH r.sector sec JOIN FETCH sec.arena a WHERE t.sportEvent.id = :eventId")
    Page<Ticket> findAllBySportEventId(Long eventId, Pageable sortedPageable);
}