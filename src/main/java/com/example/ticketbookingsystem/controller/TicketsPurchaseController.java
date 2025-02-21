package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import com.example.ticketbookingsystem.service.UserCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * REST Controller class for managing ticket purchases in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class TicketsPurchaseController {

    private final UserCartService userCartService;

    private final PurchasedTicketsService purchasedTicketsService;

    private final MessageSource messageSource;

    /**
     * Handles POST requests to commit a ticket purchase.
     *
     * @param userId The ID of the authenticated user.
     * @return A ResponseEntity containing the result of the purchase.
     */
    @PostMapping
    public ResponseEntity<String> commitPurchase(@RequestParam Long userId) {
        List<Long> ticketIds = userCartService.getTicketIds(userId);

        boolean sufficientFunds = simulatePayment();

        if (sufficientFunds) {
            purchasedTicketsService.savePurchasedTickets(ticketIds, userId);
            return ResponseEntity.ok("Purchase successful");
        } else {
            return handlePurchaseError("purchase.error.insufficientFunds");
        }
    }

    /**
     * Handles GET requests to retrieve and display all purchased tickets for the authenticated user.
     *
     * @param userId The ID of the authenticated user.
     * @return A ResponseEntity containing the list of purchased tickets.
     */
    @GetMapping("/purchasedTickets")
    public ResponseEntity<?> getPurchasedTickets(@RequestParam Long userId) {
        List<PurchasedTicketDto> purchasedTickets = purchasedTicketsService.findAllByUserId(userId);
        return ResponseEntity.ok(purchasedTickets.toString());
    }

    private ResponseEntity<String> handlePurchaseError(String errorCode) {
        Locale locale = getLocale();
        String errorMessage = messageSource.getMessage(errorCode, null, locale);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    private boolean simulatePayment() {
        return Math.random() > 0.2; // Вероятность симуляции недостатка средств на карте и ошибки транзакции (20%)
    }
}