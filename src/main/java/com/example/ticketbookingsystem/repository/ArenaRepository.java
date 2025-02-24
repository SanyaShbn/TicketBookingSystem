package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.Arena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing Arena entities.
 */
@Repository
public interface ArenaRepository extends JpaRepository<Arena, Long>, QuerydslPredicateExecutor<Arena> { }
