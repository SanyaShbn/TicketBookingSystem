package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.ArenaCreateEditDto;
import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.dto.ArenaReadDto;
import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
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

import java.util.Optional;

@Controller
@RequestMapping("/admin/arenas")
@RequiredArgsConstructor
@Slf4j
public class ArenaController {

    private final ArenaService arenaService;

    @GetMapping
    public String findAllArenas(Model model, ArenaFilter arenaFilter, Pageable pageable) {
        Page<ArenaReadDto> arenasPage = arenaService.findAll(arenaFilter, pageable);
        model.addAttribute("filter", arenaFilter);
        model.addAttribute("arenas", PageResponse.of(arenasPage));
        return JspFilesResolver.getPath("/arena-jsp/arenas");
    }

    @GetMapping("/create")
    public String showCreateArenaForm() {
        return JspFilesResolver.getPath("/arena-jsp/create-arena");
    }

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
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return JspFilesResolver.getPath("/arena-jsp/create-arena");
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating arena: {}", e.getMessage());
            return handleCreateArenaException(model, e);
        }
    }

    @GetMapping("/{id}/update")
    public String showUpdateArenaForm(@PathVariable Long id, Model model) {
        Optional<ArenaReadDto> arena = arenaService.findById(id);
        if (arena.isPresent()) {
            model.addAttribute("arena", arena.get());
            return JspFilesResolver.getPath("/arena-jsp/update-arena");
        }
        return "redirect:/admin/arenas";
    }

    @PostMapping("/{id}/update")
    public String updateArena(@PathVariable("id") Long id,
                              @ModelAttribute @Validated ArenaCreateEditDto arenaCreateEditDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("arena", arenaCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/arenas/" + id + "/update";
            }
            log.info("Updating arena {} with details: {}", id, arenaCreateEditDto);
            arenaService.updateArena(id, arenaCreateEditDto);
            return "redirect:/admin/arenas";
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return JspFilesResolver.getPath("/arena-jsp/update-arena");
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating arena: {}", e.getMessage());
            return handleUpdateArenaException(model, e);
        }
    }

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
            return JspFilesResolver.getPath("/error-jsp/error-page");
        }
    }

//    private void setRequestAttributes(String city, String capacitySortOrder, String seatsNumbSortOrder,
//                                      Pageable pageable, Model model) {
//        ArenaFilter arenaFilter = buildArenaFilter(city, capacitySortOrder, seatsNumbSortOrder);
//        Page<ArenaReadDto> arenasPage = arenaService.findAll(arenaFilter, pageable);
////        List<ArenaReadDto> arenaList = arenaService.findAll();
//        List<String> cities = arenaService.findAllArenasCities();
//
//        model.addAttribute("arenas", PageResponse.of(arenasPage));
//        model.addAttribute("cities", cities);
//        model.addAttribute("limit", 8);
//    }

    private ArenaFilter buildArenaFilter(String city, String capacitySortOrder, String seatsNumbSortOrder) {
        return new ArenaFilter(city, capacitySortOrder, seatsNumbSortOrder);
    }

    private void handleNumberFormatException(Model model) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.number.format",
                "Проверьте корректность ввода данных!"));
        model.addAttribute("errors", validationResult.getErrors());
    }

    private String handleCreateArenaException(Model model, DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        model.addAttribute("errors", sqlExceptionResult.getErrors());
        return JspFilesResolver.getPath("/arena-jsp/create-arena");
    }

    private String handleUpdateArenaException(Model model, DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        model.addAttribute("errors", sqlExceptionResult.getErrors());
        return JspFilesResolver.getPath("/arena-jsp/update-arena");
    }

    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
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