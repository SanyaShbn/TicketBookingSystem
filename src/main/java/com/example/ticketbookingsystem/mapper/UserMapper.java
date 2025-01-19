package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.Role;
import com.example.ticketbookingsystem.entity.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Mapper class for converting between {@link User} entity and {@link UserDto} DTO.
 */
public class UserMapper implements Mapper<User, UserDto> {

    private static final UserMapper INSTANCE = new UserMapper();

    private UserMapper(){}
    public static UserMapper getInstance(){
        return INSTANCE;
    }

    /**
     * Converts an {@link UserDto} to an {@link User} entity.
     *
     * @param userDto the DTO to convert
     * @return the converted {@link User} entity
     */
    @Override
    public User toEntity(UserDto userDto) {
        String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
        return User.builder()
                .email(userDto.getEmail())
                .password(hashedPassword)
                .role(Role.valueOf(userDto.getRole()))
                .build();
    }

    /**
     * Converts an {@link User} entity to an {@link UserDto}.
     *
     * @param user the entity to convert
     * @return the converted {@link UserDto}
     */
    @Override
    public UserDto toDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole().name())
                .build();
    }
}