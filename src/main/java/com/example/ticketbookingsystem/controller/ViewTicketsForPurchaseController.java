package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.service.SeatService;
import com.example.ticketbookingsystem.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller class for viewing available tickets for purchase in the Ticket Booking System application.
 */
@Controller
@RequiredArgsConstructor
public class ViewTicketsForPurchaseController {

    private final TicketService ticketService;

    private final SeatService seatService;

    /**
     * Handles GET requests to find tickets and seats for a given event and display them.
     *
     * @param eventId The ID of the event.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/view_available_tickets")
    public String findTicketsForPurchase(@RequestParam("id") Long eventId, Model model) {
        List<SeatReadDto> seats = seatService.findByEventId(eventId);
        List<TicketReadDto> tickets = ticketService.findByEventId(eventId);

        model.addAttribute("seats", seats);
        model.addAttribute("tickets", tickets);

        return "tickets-purchases-jsp/view-available-tickets";
    }
}