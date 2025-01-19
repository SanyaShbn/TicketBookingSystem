package com.example.ticketbookingsystem.mapper;

/**
 * Generic Mapper interface for converting between entity and DTO objects.
 *
 * @param <E> the type of the entity
 * @param <D> the type of the DTO
 */
public interface Mapper<E, D> {
    E toEntity(D dto);
    D toDto(E entity);
}

