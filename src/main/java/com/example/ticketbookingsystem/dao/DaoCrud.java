package com.example.ticketbookingsystem.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DaoCrud<K, E> {
    List<E> findAll();
    Optional<E> findById(K id);
    default E save(E e){
        return null;
    }
    default boolean update(E e){
        return false;
    }
    default boolean delete(K id){
        return false;
    }

}