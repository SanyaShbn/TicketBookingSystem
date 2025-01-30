package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    private final SeatService seatService;

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

    @GetMapping("/create")
    public String showCreateTicketForm(@RequestParam("eventId") Long eventId,
                                       Model model) {
        List<SeatReadDto> seatReadDtoList = seatService.findByEventIdWithNoTickets(eventId);
        model.addAttribute("eventId", eventId);
        model.addAttribute("seats", seatReadDtoList);
        return "tickets-jsp/create-ticket";
    }

    @PostMapping("/create")
    public String createTicket(@RequestParam("eventId") Long eventId,
                               @RequestParam("seatId") Long seatId,
                               @ModelAttribute TicketCreateEditDto ticketCreateEditDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("ticket", ticketCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                redirectAttributes.addAttribute("eventId", eventId);
                redirectAttributes.addAttribute("seatId", seatId);
                return "redirect:/admin/tickets/create";
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

    @PostMapping("/{id}/update")
    public String updateTicket(@RequestParam("eventId") Long eventId,
                               @RequestParam("seatId") Long seatId,
                               @PathVariable("id") Long id,
                               @ModelAttribute TicketCreateEditDto ticketCreateEditDto,
                               Model model) {
        try {
            log.info("Updating ticket {} with details: {}", id, ticketCreateEditDto);
            ticketService.updateTicket(id, ticketCreateEditDto, eventId, seatId);
            return "redirect:/admin/tickets?eventId=" + eventId;
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return "tickets-jsp/update-ticket";
        }
    }

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
            validationResult.add(Error.of("delete.sector.fail", "Ошибка удаления записи о билете!"));
            model.addAttribute("errors", validationResult.getErrors());
            return "error-jsp/error-page";
        }
    }

    private void handleNumberFormatException(Model model) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.number.format",
                "Проверьте корректность ввода данных!"));
        model.addAttribute("errors", validationResult.getErrors());
    }

}
