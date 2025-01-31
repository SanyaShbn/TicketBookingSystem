package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.SportEvent;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomSportEventRepository {
    List<SportEvent> findAllWithArena(Predicate predicate, Pageable pageable);
}

