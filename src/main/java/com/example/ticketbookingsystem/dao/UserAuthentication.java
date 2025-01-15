package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;

import java.util.Optional;


/**
 * Interface for managing user authentication.
 */
public interface UserAuthentication {

    /**
     * Finds a {@link User} entity by email.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the found user, or {@link Optional#empty()} if no user is found
     * @throws DaoCrudException if there is an error during the query
     */
    Optional<User> findByEmail(String email);

    /**
     * Saves new {@link User} entity.
     *
     * @param user the user entity to be saved
     * @throws DaoCrudException if there is an error during the save operation
     */
    void registerUser(User user);
}
