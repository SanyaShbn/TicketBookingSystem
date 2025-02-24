package com.example.ticketbookingsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * REST controller for handling internationalized messages.
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageSource messageSource;

    /**
     * Retrieves a localized message based on the provided key and language code.
     *
     * @param key the message key.
     * @param language the language code.
     * @return the localized message.
     */
    @GetMapping
    public String getMessage(@RequestParam("key") String key,
                             @RequestParam("lang") String language){
        return messageSource.getMessage(key, null, null, new Locale(language));
    }
}