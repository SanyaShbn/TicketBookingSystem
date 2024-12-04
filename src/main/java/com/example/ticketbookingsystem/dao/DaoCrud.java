package com.example.ticketbookingsystem.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DaoCrud<K, E> {
    List<E> findAll();
    Optional<E> findById(K id);
    E save(E e);
    boolean update(E e);
    boolean delete(K id);

}