package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.PurchasedTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing PurchasedTicket entities.
 */
@Repository
public interface PurchasedTicketRepository extends JpaRepository<PurchasedTicket, Long> {

    @Query("FROM PurchasedTicket pt JOIN FETCH pt.ticket t JOIN FETCH t.seat s " +
            "JOIN FETCH s.row r JOIN FETCH r.sector JOIN FETCH t.sportEvent se JOIN FETCH se.arena " +
            "WHERE pt.userId = :userId")
    List<PurchasedTicket> findAllByUserId(Long userId);
}