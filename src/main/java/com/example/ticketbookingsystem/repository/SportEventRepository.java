package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.SportEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SportEventRepository extends JpaRepository<SportEvent, Long> {
    @Query("SELECT se FROM SportEvent se JOIN FETCH se.arena")
    List<SportEvent> findAll();
}
