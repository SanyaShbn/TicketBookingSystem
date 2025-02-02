package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import com.example.ticketbookingsystem.service.UserCartService;
import com.example.ticketbookingsystem.utils.AuthenticationUtil;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing ticket purchases in the Ticket Booking System application.
 */
@Controller
@RequiredArgsConstructor
public class TicketsPurchaseController {

    private final UserCartService userCartService;

    private final PurchasedTicketsService purchasedTicketsService;

    private final AuthenticationUtil authenticationUtil;

    /**
     * Handles GET requests to show the purchase page.
     *
     * @return The name of the view to be rendered.
     */
    @GetMapping("/purchase")
    public String showPurchasePage() {
        return "tickets-purchases-jsp/purchase";
    }

    /**
     * Handles POST requests to commit a ticket purchase.
     *
     * @param model The model to hold attributes.
     * @return The redirect URL or the name of the view to be rendered.
     */
    @PostMapping("/purchase")
    public String commitPurchase(Model model) {
        Optional<UserDto> optionalUserDto = getAuthenticatedUserWithCheck(model);
        if (optionalUserDto.isEmpty()) {
            return "tickets-purchases-jsp/purchase";
        }

        UserDto user = optionalUserDto.get();

        List<Long> ticketIds = userCartService.getTicketIds(user.getId());

        boolean sufficientFunds = simulatePayment();

        if (sufficientFunds) {
            try {
                purchasedTicketsService.savePurchasedTickets(ticketIds, user.getId());
                return "redirect:/purchasedTickets";
            } catch (DaoCrudException e) {
                handlePurchaseError(model, "Ошибка! Покупка выбранных билетов уже была осуществлена");
                return "tickets-purchases-jsp/purchase";
            }
        } else {
            handlePurchaseError(model, "Ошибка! Недостаточно средств на выбранной карте");
            return "tickets-purchases-jsp/purchase";
        }
    }

    /**
     * Handles GET requests to retrieve and display all purchased tickets for the authenticated user.
     *
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/purchasedTickets")
    public String getPurchasedTickets(Model model) {
        Optional<UserDto> user = getAuthenticatedUserWithCheck(model);
        if (user.isEmpty()) {
            return "tickets-purchases-jsp/purchased-tickets";
        }

        try {
            List<PurchasedTicketDto> purchasedTickets = purchasedTicketsService.findAllByUserId(user.get().getId());
            model.addAttribute("purchasedTickets", purchasedTickets);
        } catch (DaoCrudException e) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("get.purchasedTickets.error",
                    "Ошибка! Не удалось получить информацию о приобретенных вами билетах. " +
                            "Обратитесь к администратору сайта"));
            model.addAttribute("errors", validationResult.getErrors());
            return "tickets-purchases-jsp/purchased-tickets";
        }

        return "tickets-purchases-jsp/purchased-tickets";
    }

    private boolean simulatePayment() {
        return Math.random() > 0.2; // Вероятность симуляции недостатка средств на карте и ошибки транзакции (20%)
    }

    private void handlePurchaseError(Model model, String errorMessage) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("purchase.error", errorMessage));
        model.addAttribute("errors", validationResult.getErrors());
    }

    private Optional<UserDto> getAuthenticatedUserWithCheck(Model model) {
        Optional<UserDto> user = authenticationUtil.getAuthenticatedUser();
        if (user.isEmpty()) {
            model.addAttribute("errors", "User not authenticated");
        }
        return user;
    }
}