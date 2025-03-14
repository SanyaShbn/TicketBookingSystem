package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller class for managing user authentication and registration in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Retrieves the ID of the user by provided username (email).
     *
     * @param username The value of user's username (in our case, email).
     * @return A ResponseEntity containing the required user's ID.
     */
    @GetMapping("/id")
    public ResponseEntity<Long> getUserIdByUsername(@RequestParam String username) {
        return userService.findByEmail(username).map(UserDto::getId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Handles POST requests to register a new user account.
     *
     * @param userDto The user data transfer object.
     * @param bindingResult The binding result for validation.
     * @return A ResponseEntity containing the registered UserDto and HTTP status.
     */
    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUserAccount(@RequestBody @Validated UserDto userDto,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userDto);
        }
        userService.registerNewUserAccount(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
}