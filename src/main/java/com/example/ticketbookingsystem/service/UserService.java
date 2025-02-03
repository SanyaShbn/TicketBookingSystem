package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.Role;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.mapper.UserMapper;
import com.example.ticketbookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing users login and registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    /**
     * Checks whether such user is registered in the system.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return an {@link Optional} containing the found user DTO, or empty if not found
     */
    public Optional<UserDto> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> BCrypt.checkpw(password, user.getPassword()))
                .map(userMapper::toDto);
    }

    /**
     * Finds a user by their email.
     *
     * @param email The email of the user.
     * @return An {@link Optional} containing the found user DTO, or empty if not found.
     */
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    /**
     * Registers a new user account.
     *
     * @param userDto The user data transfer object.
     * @throws UserAlreadyExistException If a user with the same email already exists.
     */
    public void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: "
                    + userDto.getEmail());
        }

        User user = userMapper.toEntity(userDto);
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
