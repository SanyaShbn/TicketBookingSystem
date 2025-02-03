package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketFilter;
import com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.SeatService;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Controller class for managing tickets in the Ticket Booking System application.
 */
@Controller
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    private final SeatService seatService;

    private final MessageSource messageSource;

    /**
     * Handles GET requests to retrieve and display all tickets for a specific event.
     *
     * @param eventId The ID of the event.
     * @param ticketFilter The filter criteria for tickets.
     * @param pageable The pagination information.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping
    public String findAllTickets(@RequestParam("eventId") Long eventId,
                                 TicketFilter ticketFilter,
                                 Pageable pageable,
                                 Model model) {
        Page<TicketReadDto> ticketReadDtoList = ticketService.findAll(eventId, ticketFilter, pageable);
        model.addAttribute("tickets", PageResponse.of(ticketReadDtoList));
        model.addAttribute("filter", ticketFilter);
        return "tickets-jsp/tickets";
    }

    /**
     * Handles GET requests to show the form for creating a new ticket.
     *
     * @param eventId The ID of the event.
     * @param ticketCreateEditDto The ticket data transfer object.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/create")
    public String showCreateTicketForm(@RequestParam("eventId") Long eventId,
                                       @ModelAttribute("ticket") TicketCreateEditDto ticketCreateEditDto,
                                       Model model) {
        List<SeatReadDto> seatReadDtoList = seatService.findByEventIdWithNoTickets(eventId);
        model.addAttribute("eventId", eventId);
        model.addAttribute("seats", seatReadDtoList);
        if (!model.containsAttribute("sector")) {
            model.addAttribute("sector", SectorCreateEditDto.builder().build());
        }
        return "tickets-jsp/create-ticket";
    }

    /**
     * Handles POST requests to create a new ticket.
     *
     * @param eventId The ID of the event.
     * @param seatId The ID of the seat.
     * @param ticketCreateEditDto The ticket data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/create")
    public String createTicket(@RequestParam("eventId") Long eventId,
                               @RequestParam("seatId") Long seatId,
                               @ModelAttribute @Validated TicketCreateEditDto ticketCreateEditDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("ticket", ticketCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/tickets/create?eventId=" + eventId + "&seatId=" + seatId;
            }
            log.info("Creating new ticket with details: {}", ticketCreateEditDto);
            ticketService.createTicket(ticketCreateEditDto, eventId, seatId);
            return "redirect:/admin/tickets?eventId=" + eventId;
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return "tickets-jsp/create-ticket";
        }
    }

    /**
     * Handles GET requests to show the form for updating an existing ticket.
     *
     * @param eventId The ID of the event.
     * @param seatId The ID of the seat.
     * @param id The ID of the ticket to be updated.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/{id}/update")
    public String showUpdateTicketForm(@RequestParam("eventId") Long eventId,
                                       @RequestParam("seatId") Long seatId,
                                       @PathVariable Long id,
                                       Model model) {
        Optional<TicketReadDto> ticket = ticketService.findById(id);
        if (ticket.isPresent()) {
            List<SeatReadDto> seatReadDtoList = seatService.findAllByEventIdWhenUpdate(eventId, seatId);
            model.addAttribute("ticket", ticket.get());
            model.addAttribute("eventId", eventId);
            model.addAttribute("seatId", seatId);
            model.addAttribute("seats", seatReadDtoList);
            return "tickets-jsp/update-ticket";
        }
        return "redirect:/admin/tickets?eventId=" + eventId;
    }

    /**
     * Handles POST requests to update an existing ticket.
     *
     * @param eventId The ID of the event.
     * @param seatId The ID of the seat.
     * @param id The ID of the ticket to be updated.
     * @param ticketCreateEditDto The ticket data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/update")
    public String updateTicket(@RequestParam("eventId") Long eventId,
                               @RequestParam("seatId") Long seatId,
                               @PathVariable("id") Long id,
                               @ModelAttribute @Validated TicketCreateEditDto ticketCreateEditDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/tickets/" + id + "/update?eventId=" + eventId + "&seatId=" + seatId;
            }
            log.info("Updating ticket {} with details: {}", id, ticketCreateEditDto);
            ticketService.updateTicket(id, ticketCreateEditDto, eventId, seatId);
            return "redirect:/admin/tickets?eventId=" + eventId;
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return "tickets-jsp/update-ticket";
        }
    }

    /**
     * Handles POST requests to delete an existing ticket.
     *
     * @param eventId The ID of the sport event which the ticket belongs to.
     * @param id The ID of the ticket to be deleted.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/delete")
    public String deleteTicket(@RequestParam("eventId") Long eventId,
                               @PathVariable("id") Long id,
                               Model model) {
        try {
            log.info("Deleting ticket with id: {}", id);
            ticketService.deleteTicket(id);
            return "redirect:/admin/tickets?eventId=" + eventId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while trying to delete ticket: {}", e.getMessage());
            ValidationResult validationResult = new ValidationResult();

            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("delete.ticket.fail", null, locale);

            validationResult.add(Error.of("delete.ticket.fail", errorMessage));
            model.addAttribute("errors", validationResult.getErrors());
            return "error-jsp/error-page";
        }
    }

    private void handleNumberFormatException(Model model) {
        ValidationResult validationResult = new ValidationResult();

        Locale locale = getLocale();
        String errorMessage = messageSource.getMessage("invalid.number.format", null, locale);

        validationResult.add(Error.of("invalid.number.format", errorMessage));
        model.addAttribute("errors", validationResult.getErrors());
    }

}
