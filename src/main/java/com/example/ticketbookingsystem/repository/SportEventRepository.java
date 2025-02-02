package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.SportEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing Sport Event entities.
 */
@Repository
public interface SportEventRepository extends JpaRepository<SportEvent, Long>, CustomSportEventRepository {
}