package com.example.ticketbookingsystem.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface DaoCrud<K, E> {
    default List<E> findAll() {
        return Collections.emptyList();
    }
    default Optional<E> findById(K id){
        return Optional.empty();
    }
    default E save(E e){
        return e;
    }
    default boolean update(E e){
        return false;
    }
    default boolean delete(K id){
        return false;
    }

}