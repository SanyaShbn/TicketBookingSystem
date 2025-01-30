package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.*;
import com.example.ticketbookingsystem.dto.row_dto.RowCreateEditDto;
import com.example.ticketbookingsystem.dto.row_dto.RowFilter;
import com.example.ticketbookingsystem.dto.row_dto.RowReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.RowService;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/rows")
@RequiredArgsConstructor
@Slf4j
public class RowController {

    private final RowService rowService;

    @GetMapping
    public String findAllRows(@RequestParam("sectorId") Long sectorId,
                              RowFilter rowFilter,
                              Pageable pageable,
                              Model model) {
        Page<RowReadDto> rowsPage = rowService.findAll(sectorId, rowFilter, pageable);
        model.addAttribute("filter", rowFilter);
        model.addAttribute("rows", PageResponse.of(rowsPage));
        return "rows-jsp/rows";
    }

    @GetMapping("/create")
    public String showCreateRowForm(@RequestParam("arenaId") Long arenaId,
                                    @RequestParam("sectorId") Long sectorId,
                                    Model model) {
        model.addAttribute("arenaId", arenaId);
        model.addAttribute("sectorId", sectorId);
        return "rows-jsp/create-row";
    }

    @PostMapping("/create")
    public String createRow(@RequestParam("arenaId") Long arenaId,
                            @RequestParam("sectorId") Long sectorId,
                            @ModelAttribute RowCreateEditDto rowCreateEditDto,
                            Model model) {
        try {
            log.info("Creating new row with details: {}", rowCreateEditDto);
            rowService.createRow(rowCreateEditDto, sectorId);
            return "redirect:/admin/rows?arenaId=" + arenaId + "&sectorId=" + sectorId;
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return "rows-jsp/create-row";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating sector: {}", e.getMessage());
            return handleCreateRowException(model, e);
        }
    }

    @GetMapping("/{id}/update")
    public String showUpdateRowForm(@RequestParam("arenaId") Long arenaId,
                                    @RequestParam("sectorId") Long sectorId,
                                    @PathVariable Long id,
                                    Model model) {
        Optional<RowReadDto> row = rowService.findById(id);
        if (row.isPresent()) {
            model.addAttribute("row", row.get());
            model.addAttribute("arenaId", arenaId);
            model.addAttribute("sectorId", sectorId);
            return "rows-jsp/update-row";
        }
        return "redirect:/admin/rows?arenaId=" + arenaId + "&sectorId=" + sectorId;
    }

    @PostMapping("/{id}/update")
    public String updateRow(@RequestParam("arenaId") Long arenaId,
                            @RequestParam("sectorId") Long sectorId,
                            @PathVariable("id") Long id,
                            @ModelAttribute RowCreateEditDto rowCreateEditDto,
                            Model model) {
        try {
            log.info("Updating row {} with details: {}", id, rowCreateEditDto);
            rowService.updateRow(id, rowCreateEditDto, sectorId);
            return "redirect:/admin/rows?arenaId=" + arenaId + "&sectorId=" + sectorId;
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            handleNumberFormatException(model);
            return "rows-jsp/update-row";
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating row: {}", e.getMessage());
            return handleUpdateRowException(model, e);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteRow(@RequestParam("arenaId") Long arenaId,
                            @RequestParam("sectorId") Long sectorId,
                            @PathVariable("id") Long id,
                            Model model) {
        try {
            log.info("Deleting row with id: {}", id);
            rowService.deleteRow(id);
            return "redirect:/admin/rows?arenaId=" + arenaId + "&sectorId=" + sectorId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while trying to delete row: {}", e.getMessage());
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.row.fail",
                    "Ошибка! Данные о выбранном ряде арены нельзя удалить. На него уже добавлены билеты " +
                            "в разделе 'Предстоящие спортивные события'"));
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

    private String handleCreateRowException(Model model, DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        model.addAttribute("errors", sqlExceptionResult.getErrors());
        return "rows-jsp/create-row";
    }

    private String handleUpdateRowException(Model model, DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        model.addAttribute("errors", sqlExceptionResult.getErrors());
        return "rows-jsp/update-row";
    }

    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_ROWS" -> sqlExceptionResult.add(Error.of("create_edit.row.fail",
                        "Ошибка! Заданное максимальное значение рядов сектора превышено. " +
                                "Вы не можете создать новый ряд"));
                case "ERROR_CHECK_SEATS" -> sqlExceptionResult.add(Error.of("create_edit.row.fail",
                        "Ошибка! Суммарное количество мест рядов сектора не может превышать заданное " +
                                "маскимальное количество доступных мест в секторе. " +
                                "Проверьте корректность ввода данных"));
                case "ERROR_CHECK_ROW_NUMBER" -> sqlExceptionResult.add(Error.of("create_edit.row.fail",
                        "Ошибка! Ряд с таким номером уже существует в данном секторе. " +
                                "Проверьте корректность ввода данных"));
                default -> sqlExceptionResult.add(Error.of("create_edit.row.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("create_edit.row.fail", "Unknown sql exception"));
        }
    }

    private String getErrorType(String errorMessage) {
        if (errorMessage.contains("ERROR_CHECK_ROWS")) {
            return "ERROR_CHECK_ROWS";
        } else if (errorMessage.contains("ERROR_CHECK_SEATS")) {
            return "ERROR_CHECK_SEATS";
        } else if (errorMessage.contains("ERROR_CHECK_ROW_NUMBER")) {
            return "ERROR_CHECK_ROW_NUMBER";
        } else {
            return "UNKNOWN_ERROR";
        }
    }
}
