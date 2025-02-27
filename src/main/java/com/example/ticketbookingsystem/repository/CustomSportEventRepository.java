package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.SportEvent;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Custom repository interface for SportEvent entities.
 */
@Repository
public interface CustomSportEventRepository {
    Page<SportEvent> findAllWithArena(Predicate predicate, Pageable pageable);
}

