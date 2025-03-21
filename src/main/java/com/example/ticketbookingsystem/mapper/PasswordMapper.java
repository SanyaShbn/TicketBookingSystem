package com.example.ticketbookingsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Mapper class for password hashing in the Ticket Booking System application.
 */
@Mapper(componentModel = "spring")
public class PasswordMapper {

    @Named("toHash")
    public String toHash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

}