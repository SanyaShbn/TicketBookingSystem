package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.UserDao;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.mapper.UserMapper;
import com.example.ticketbookingsystem.validator.RegisterUserValidator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Service class for managing users login and registration.
 */
public class UserService {
    private static final UserService INSTANCE = new UserService();
    private final RegisterUserValidator registerUserValidator = RegisterUserValidator.getInstance();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private UserService(){}
    public static UserService getInstance(){
        return INSTANCE;
    }

    /**
     * Creates a new user.
     *
     * @param userDto the DTO of the user to create
     */
    public void createUser(UserDto userDto){
        var validationResult = registerUserValidator.isValid(userDto);
        if(!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        User user = userMapper.toEntity(userDto);
        userDao.registerUser(user);
    }

    /**
     * Finds a user by its email.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the found user, or empty if not found
     */
    public Optional<User> findByEmail(String email){
        return userDao.findByEmail(email);
    }

    /**
     * Checks whether such user is registered in the system.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return an {@link Optional} containing the found user DTO, or empty if not found
     */
    public Optional<UserDto> login(String email, String password) {
        return userDao.findByEmail(email)
                .filter(user -> BCrypt.checkpw(password, user.getPassword()))
                .map(userMapper::toDto);
    }
}
