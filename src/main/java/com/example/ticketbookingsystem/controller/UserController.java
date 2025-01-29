package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.service.UserService;
import com.example.ticketbookingsystem.validator.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", UserDto.builder().build());
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Validated UserDto userDto, Model model) {
        try {
            userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            model.addAttribute("errors", Error.of("user.registration.fail", uaeEx.getMessage()));
            return "registration";
        }
        return "redirect:/login";
    }
}