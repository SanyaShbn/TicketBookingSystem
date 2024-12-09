package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.UserDao;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.validator.RegisterUserValidator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UserService {
    private final static UserService INSTANCE = new UserService();
    private final RegisterUserValidator registerUserValidator = RegisterUserValidator.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private UserService(){}
    public static UserService getInstance(){
        return INSTANCE;
    }
    public void createUser(UserDto userDto){
        var validationResult = registerUserValidator.isValid(userDto);
        if(!validationResult.isValid()){
            throw new ValidationException(validationResult.getErrors());
        }
        User user = buildUserFromDto(userDto);
        userDao.save(user);
    }
    public Optional<User> findByEmail(String email){
        return userDao.findByEmail(email);
    }
    public String getUserRole(Long userId){
        return userDao.getUserRole(userId);
    }
    public Optional<UserDto> login(String email, String password) {
        return userDao.findByEmail(email)
                .filter(user -> BCrypt.checkpw(password, user.getPassword()))
                .map(this::mapToDto);
    }

    private User buildUserFromDto(UserDto userDto) {
        String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
        return User.builder()
                .email(userDto.getEmail())
                .password(hashedPassword)
                .build();
    }
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
