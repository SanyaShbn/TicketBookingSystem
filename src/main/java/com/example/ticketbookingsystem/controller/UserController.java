package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * REST Controller class for managing user authentication and registration in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Handles POST requests to register a new user account.
     *
     * @param userDto The user data transfer object.
     * @param bindingResult The binding result for validation.
     * @return A ResponseEntity containing the registered UserDto and HTTP status.
     */
    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUserAccount(@ModelAttribute("user") @Validated UserDto userDto,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userDto);
        }
        try {
            userService.registerNewUserAccount(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userDto);
        }
    }
}