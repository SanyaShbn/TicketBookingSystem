package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.arena_dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaFilter;
import com.example.ticketbookingsystem.dto.arena_dto.ArenaReadDto;
import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing arenas in the Ticket Booking System application.
 */
@Controller
@RequestMapping("/admin/arenas")
@RequiredArgsConstructor
@Slf4j
public class ArenaController {

    private final ArenaService arenaService;

    /**
     * Handles GET requests to retrieve and display all arenas.
     *
     * @param model The model to hold attributes.
     * @param arenaFilter The filter criteria for arenas.
     * @param pageable The pagination information.
     * @return The name of the view to be rendered.
     */
    @GetMapping
    public String findAllArenas(Model model, ArenaFilter arenaFilter, Pageable pageable) {
        Page<ArenaReadDto> arenasPage = arenaService.findAll(arenaFilter, pageable);
        model.addAttribute("filter", arenaFilter);
        model.addAttribute("arenas", PageResponse.of(arenasPage));
        return "arena-jsp/arenas";
    }

    /**
     * Handles GET requests to show the form for creating a new arena.
     *
     * @param arena The arena data transfer object.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/create")
    public String showCreateArenaForm(@ModelAttribute("arena") ArenaCreateEditDto arena,
                                      Model model) {
        if (!model.containsAttribute("arena")) {
            model.addAttribute("arena", ArenaCreateEditDto.builder().build());
        }
        return "arena-jsp/create-arena";
    }

    /**
     * Handles POST requests to create a new arena.
     *
     * @param arenaCreateEditDto The arena data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/create")
    public String createArena(@ModelAttribute @Validated ArenaCreateEditDto arenaCreateEditDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("arena", arenaCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/arenas/create";
            }
            log.info("Creating new arena with details: {}", arenaCreateEditDto);
            arenaService.createArena(arenaCreateEditDto);
            return "redirect:/admin/arenas";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating arena: {}", e.getMessage());
            return handleCreateArenaException(model, arenaCreateEditDto, e);
        }
    }

    /**
     * Handles GET requests to show the form for updating an existing arena.
     *
     * @param id The ID of the arena to be updated.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/{id}/update")
    public String showUpdateArenaForm(@PathVariable Long id, Model model) {
        Optional<ArenaReadDto> arena = arenaService.findById(id);
        if (arena.isPresent()) {
            model.addAttribute("arena", arena.get());
            return "arena-jsp/update-arena";
        }
        return "redirect:/admin/arenas";
    }

    /**
     * Handles POST requests to update an existing arena.
     *
     * @param id The ID of the arena to be updated.
     * @param arenaCreateEditDto The arena data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/update")
    public String updateArena(@PathVariable("id") Long id,
                              @ModelAttribute @Validated ArenaCreateEditDto arenaCreateEditDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/arenas/" + id + "/update";
            }
            log.info("Updating arena {} with details: {}", id, arenaCreateEditDto);
            arenaService.updateArena(id, arenaCreateEditDto);
            return "redirect:/admin/arenas";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating arena: {}", e.getMessage());
            return handleUpdateArenaException(id, e, redirectAttributes);
        }
    }

    /**
     * Handles POST requests to delete an existing arena.
     *
     * @param id The ID of the arena to be deleted.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/delete")
    public String deleteArena(@PathVariable("id") Long id, Model model) {
        try {
            log.info("Deleting arena with id: {}", id);
            arenaService.deleteArena(id);
            return "redirect:/admin/arenas";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while trying to delete arena: {}", e.getMessage());
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.arena.fail",
                    "Ошибка! Данные о выбранной арене нельзя удалить. Для нее уже запланированы события " +
                            "в разделе 'Предстоящие спортивные события'"));
            model.addAttribute("errors", validationResult.getErrors());
            return "error-jsp/error-page";
        }
    }

    private String handleCreateArenaException(Model model,
                                              ArenaCreateEditDto arenaCreateEditDto,
                                              DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifyDataAccessException(e.getMessage(), sqlExceptionResult);
        model.addAttribute("errors", sqlExceptionResult.getErrors());
        model.addAttribute("arena", arenaCreateEditDto);
        return "arena-jsp/create-arena";
    }

    private String handleUpdateArenaException(Long id,
                                              DaoCrudException e,
                                              RedirectAttributes redirectAttributes) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifyDataAccessException(e.getMessage(), sqlExceptionResult);
        redirectAttributes.addFlashAttribute("errors", sqlExceptionResult.getErrors());
        return "redirect:/admin/arenas/" + id + "/update";
    }

    private void specifyDataAccessException(String errorMessage, ValidationResult sqlExceptionResult) {
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_ARENA_NAME" -> sqlExceptionResult.add(Error.of("create_edit.arena.fail",
                        "Ошибка! Имя арены должно быть уникальным. Проверьте корректность ввода данных"));
                case "ERROR_CHECK_CAPACITY" -> sqlExceptionResult.add(Error.of("create_edit.arena.fail",
                        "Ошибка! Общее максимально возможное количество мест в секторах для " +
                                "заданной арены превышает ее вместимость. Проверьте корректность ввода данных"));
                default -> sqlExceptionResult.add(Error.of("create_edit.row.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("create_edit.arena.fail", "Unknown sql exception"));
        }
    }

    private String getErrorType(String errorMessage) {
        if (errorMessage.contains("unique_name")) {
            return "ERROR_CHECK_ARENA_NAME";
        } else if (errorMessage.contains("ERROR_CHECK_CAPACITY")) {
            return "ERROR_CHECK_CAPACITY";}
        else {
            return "UNKNOWN_ERROR";
        }
    }
}