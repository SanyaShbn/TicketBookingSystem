package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.ArenaDto;
import com.example.ticketbookingsystem.dto.ArenaFilter;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/arenas")
@RequiredArgsConstructor
public class ArenaController {

    private final ArenaService arenaService;

    @GetMapping
    public String getAllArenas(@RequestParam(value = "city", required = false) String city,
                               @RequestParam(value = "capacitySortOrder", required = false) String capacitySortOrder,
                               @RequestParam(value = "seatsNumbSortOrder", required = false) String seatsNumbSortOrder,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               Model model) {
        setRequestAttributes(city, capacitySortOrder, seatsNumbSortOrder, page, model);
        return JspFilesResolver.getPath("/arena-jsp/arenas");
    }

    @GetMapping("/create")
    public String showCreateArenaForm() {
        return JspFilesResolver.getPath("/arena-jsp/create-arena");
    }

    @PostMapping("/create")
    public String createArena(@ModelAttribute ArenaDto arenaDto, Model model) {
        try {
            arenaService.createArena(arenaDto);
            return "redirect:/admin/arenas";
        } catch (NumberFormatException e) {
            return handleNumberFormatException(model);
        } catch (DaoCrudException e) {
            return handleCreateArenaException(model, e);
        } catch (ValidationException e) {
            return handleValidationException(model, e);
        }
    }

    @GetMapping("/{id}/update")
    public String showUpdateArenaForm(@PathVariable Long id, Model model) {
        Optional<ArenaDto> arena = arenaService.findById(id);
        if (arena.isPresent()) {
            model.addAttribute("arena", arena.get());
            return JspFilesResolver.getPath("/arena-jsp/update-arena");
        }
        return "redirect:/admin/arenas";
    }

    @PostMapping("/{id}/update")
    public String updateArena(@PathVariable Long id, @ModelAttribute ArenaDto arenaDto, Model model) {
        try {
            arenaService.updateArena(arenaDto);
            return "redirect:/admin/arenas";
        } catch (NumberFormatException e) {
            return handleNumberFormatException(model);
        } catch (DaoCrudException e) {
            return handleUpdateArenaException(model, e);
        } catch (ValidationException e) {
            return handleValidationException(model, e);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteArena(@PathVariable Long id, Model model) {
        try {
            arenaService.deleteArena(id);
            return "redirect:/admin/arenas";
        } catch (DaoCrudException e) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.arena.fail",
                    "Ошибка! Данные о выбранной арене нельзя удалить. Для нее уже запланированы события " +
                            "в разделе 'Предстоящие спортивные события'"));
            model.addAttribute("errors", validationResult.getErrors());
            return JspFilesResolver.getPath("/error-jsp/error-page");
        }
    }

    private void setRequestAttributes(String city, String capacitySortOrder, String seatsNumbSortOrder, int page, Model model) {
        ArenaFilter arenaFilter = buildArenaFilter(city, capacitySortOrder, seatsNumbSortOrder, page);
//        List<ArenaDto> arenaList = arenaService.findAll(arenaFilter);
        List<ArenaDto> arenaList = arenaService.findAll();
        List<String> cities = arenaService.findAllArenasCities();

        model.addAttribute("arenas", arenaList);
        model.addAttribute("cities", cities);
        model.addAttribute("limit", 8);
        model.addAttribute("page", page);
    }

    private ArenaFilter buildArenaFilter(String city, String capacitySortOrder, String seatsNumbSortOrder, int page) {
        int limit = 8;
        int offset = (page - 1) * limit;

        return new ArenaFilter(city, capacitySortOrder, seatsNumbSortOrder, limit, offset);
    }

    private String handleNumberFormatException(Model model) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.number.format",
                "Проверьте корректность ввода данных!"));
        model.addAttribute("errors", validationResult.getErrors());
        return JspFilesResolver.getPath("/arena-jsp/create-arena");
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

    private String handleValidationException(Model model, ValidationException e) {
        model.addAttribute("errors", e.getErrors());
        return JspFilesResolver.getPath("/arena-jsp/create-arena");
    }

    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_ARENA_NAME" -> sqlExceptionResult.add(Error.of("create.arena.fail",
                        "Ошибка! Имя арены должно быть уникальным. Проверьте корректность ввода данных"));
                case "ERROR_CHECK_CAPACITY" -> sqlExceptionResult.add(Error.of("create.arena.fail",
                        "Ошибка! Общее максимально возможное количество мест в секторах для " +
                                "заданной арены превышает ее вместимость. Проверьте корректность ввода данных"));
                default -> sqlExceptionResult.add(Error.of("create.row.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("create.arena.fail", "Unknown sql exception"));
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