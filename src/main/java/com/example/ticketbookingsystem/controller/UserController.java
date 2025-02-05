package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Controller class for managing user authentication and registration in the Ticket Booking System application.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final MessageSource messageSource;

    /**
     * Handles GET requests to show the login page.
     *
     * @param error An optional error message.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model){
        if (error != null) {
            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("login.fail", null, locale);
            model.addAttribute("error", errorMessage);
        }
        return "/login";
    }

    /**
     * Handles GET requests to show the registration form.
     *
     * @param model The model to hold attributes.
     * @param userDto The user data transfer object.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/registration")
    public String showRegistrationForm(Model model,
                                       @ModelAttribute("user") UserDto userDto) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", UserDto.builder().build());
        }
        return "registration";
    }

    /**
     * Handles POST requests to register a new user account.
     *
     * @param userDto The user data transfer object.
     * @param bindingResult The binding result for validation.
     * @param model The model to hold attributes.
     * @return The redirect URL or the name of the view to be rendered.
     */
    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Validated UserDto userDto,
                                      BindingResult bindingResult,
                                      Model model) {
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("user", userDto);
                model.addAttribute("errors", bindingResult.getAllErrors());
                return "registration";
            }
            userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("registration.fail", null, locale);
            model.addAttribute("user", userDto);
            model.addAttribute("error", errorMessage);
            return "registration";
        }
        return "redirect:/login";
    }
}