package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing sport events in the Ticket Booking System application.
 */
@Controller
@RequestMapping("/admin/sport_events")
@RequiredArgsConstructor
@Slf4j
public class SportEventController {

    private final SportEventService sportEventService;

    private final ArenaService arenaService;

    /**
     * Handles GET requests to retrieve and display all sport events.
     *
     * @param model The model to hold attributes.
     * @param sportEventFilter The filter criteria for sport events.
     * @param pageable The pagination information.
     * @return The name of the view to be rendered.
     */
    @GetMapping
    public String findAllSportEvents(Model model, SportEventFilter sportEventFilter, Pageable pageable) {
        List<ArenaReadDto> arenaReadDtoList = arenaService.findAll();
        Page<SportEventReadDto> sportEventsPage = sportEventService.findAll(sportEventFilter, pageable);

        model.addAttribute("filter", sportEventFilter);
        model.addAttribute("sport_events", PageResponse.of(sportEventsPage));
        model.addAttribute("arenas", arenaReadDtoList);
        return "sport-events-jsp/sport_events";
    }

    /**
     * Handles GET requests to show the form for creating a new sport event.
     *
     * @param sportEventCreateEditDto The sport event data transfer object.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/create")
    public String showCreateSportEventForm(@ModelAttribute("sport_event") SportEventCreateEditDto sportEventCreateEditDto,
                                           Model model) {
        List<ArenaReadDto> arenaReadDtoList = arenaService.findAll();
        model.addAttribute("arenas", arenaReadDtoList);
        if (!model.containsAttribute("sport_event")) {
            model.addAttribute("sport_event", ArenaCreateEditDto.builder().build());
        }
        return "sport-events-jsp/create-sport-event";
    }

    /**
     * Handles POST requests to create a new sport event.
     *
     * @param arenaId The ID of the arena to which the sport event belongs.
     * @param sportEventCreateEditDto The sport event data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/create")
    public String createSportEvent(@RequestParam("arenaId") Long arenaId,
                                   @ModelAttribute @Validated SportEventCreateEditDto sportEventCreateEditDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("sport_event", sportEventCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/sport_events/create";
            }
            log.info("Creating new sporting event with details: {}", sportEventCreateEditDto);
            sportEventService.createSportEvent(sportEventCreateEditDto, arenaId);
            return "redirect:/admin/sport_events";
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return "sport-events-jsp/create-sport-event";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating sporting event: {}", e.getMessage());
            return handleCreateSportEventException(sportEventCreateEditDto, redirectAttributes, e);
        }
    }

    /**
     * Handles GET requests to show the form for updating an existing sport event.
     *
     * @param id The ID of the sport event to be updated.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/{id}/update")
    public String showUpdateSportEventForm(@PathVariable Long id, Model model) {
        Optional<SportEventReadDto> sportEventReadDto = sportEventService.findById(id);
        if (sportEventReadDto.isPresent()) {
            List<ArenaReadDto> arenaReadDtoList = arenaService.findAll();
            model.addAttribute("arenas", arenaReadDtoList);
            model.addAttribute("sport_event", sportEventReadDto.get());
            return "sport-events-jsp/update-sport-event";
        }
        return "redirect:/admin/sport_events";
    }

    /**
     * Handles POST requests to update an existing sport event.
     *
     * @param arenaId The ID of the arena to which the sport event belongs.
     * @param id The ID of the sport event to be updated.
     * @param sportEventCreateEditDto The sport event data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/update")
    public String updateSportEvent(@RequestParam("arenaId") Long arenaId,
                                   @PathVariable("id") Long id,
                                   @ModelAttribute @Validated SportEventCreateEditDto sportEventCreateEditDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/sport_events/" + id + "/update";
            }
            log.info("Updating sporting event {} with details: {}", id, sportEventCreateEditDto);
            sportEventService.updateSportEvent(id, sportEventCreateEditDto, arenaId);
            return "redirect:/admin/sport_events";
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return "sport-events-jsp/update-sport-event";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating sporting event: {}", e.getMessage());
            return handleUpdateSportEventException(id, e, redirectAttributes);
        }
    }

    /**
     * Handles POST requests to delete an existing sport event.
     *
     * @param id The ID of the sport event to be deleted.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/delete")
    public String deleteSportEvent(@PathVariable("id") Long id, Model model) {
        try {
            log.info("Deleting sporting event with id: {}", id);
            sportEventService.deleteSportEvent(id);
            return "redirect:/admin/sport_events";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while trying to delete sporting event: {}", e.getMessage());
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.sport.event.fail",
                    "Выбранное событие нельзя удалить, на него уже доступна продажа билетов. " +
                            "Если хотите выполнить удаление, необходимо удалить всю информацию о продаваемых билетах"));
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

    private String handleCreateSportEventException(SportEventCreateEditDto sportEventCreateEditDto,
                                                   RedirectAttributes redirectAttributes,
                                                   DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        redirectAttributes.addFlashAttribute("sport_event", sportEventCreateEditDto);
        redirectAttributes.addFlashAttribute("errors", sqlExceptionResult.getErrors());
        return "redirect:/admin/sport_events/create";
    }

    private String handleUpdateSportEventException(Long id,
                                                   DaoCrudException e,
                                                   RedirectAttributes redirectAttributes) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        redirectAttributes.addFlashAttribute("errors", sqlExceptionResult.getErrors());
        return "redirect:/admin/sport_events/" + id + "/update";
    }

    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "EVENT_TIME_EXCEPTION" -> sqlExceptionResult.add(Error.of("create_edit.sport.event.fail",
                        "Создаваемое событие не может пересекаться по времени с уже запланированными событиями" +
                        " для выбранной арены (минимальный интервал для одной даты - 3 часа). " +
                        "Проверьте корректность ввода данных!"));
                case "EVENT_REFERENCE_EXCEPTION" -> sqlExceptionResult.add(Error.of("create_edit.sport.event.fail",
                        "Выбранное событие нельзя редактировать, на него уже доступна продажа билетов. " +
                        "Если хотите выполнить редактирование, необходимо удалить всю информацию о продаваемых билетах"));
                default -> sqlExceptionResult.add(Error.of("create.row.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("create_edit.sport.event.fail", "Unknown sql exception"));
        }
    }

    private String getErrorType(String errorMessage) {
        if (errorMessage.contains("EVENT_TIME_EXCEPTION")) {
            return "EVENT_TIME_EXCEPTION";
        } else if (errorMessage.contains("EVENT_REFERENCE_EXCEPTION")) {
            return "EVENT_REFERENCE_EXCEPTION";}
        else {
            return "UNKNOWN_ERROR";
        }
    }
}
