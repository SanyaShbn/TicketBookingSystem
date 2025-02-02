package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model){
        if (error != null) {
            model.addAttribute("error", "Неверный логин или пароль.");
        }
        return "login";
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model,
                                       @ModelAttribute("user") UserDto userDto) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", UserDto.builder().build());
        }
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Validated UserDto userDto,
                                      BindingResult bindingResult,
                                      Model model) {
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("user", userDto);
                model.addAttribute("error", "Confirm password doesn't match the original one");
                return "registration";
            }
            userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            model.addAttribute("user", userDto);
            model.addAttribute("error", "User already exists");
            return "registration";
        }
        return "redirect:/login";
    }
}