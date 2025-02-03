package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SportEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller class for managing sport events available for ticket purchase in the Ticket Booking System application.
 */
@Controller
@RequiredArgsConstructor
public class SportEventsForTicketPurchaseController {

    private final SportEventService sportEventService;

    private final ArenaService arenaService;

    /**
     * Handles GET requests to retrieve and display all sport events available for ticket purchase.
     *
     * @param model The model to hold attributes.
     * @param sportEventFilter The filter criteria for sport events.
     * @param pageable The pagination information.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/view_available_events")
    public String findAllSportEventsForTicketPurchase(Model model, SportEventFilter sportEventFilter, Pageable pageable) {
        List<ArenaReadDto> arenaReadDtoList = arenaService.findAll();
        Page<SportEventReadDto> sportEventsPage = sportEventService.findAll(sportEventFilter, pageable);

        model.addAttribute("filter", sportEventFilter);
        model.addAttribute("sport_events", PageResponse.of(sportEventsPage));
        model.addAttribute("arenas", arenaReadDtoList);
        return "tickets-purchases-jsp/view-available-events";
    }
}
