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
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Controller class for managing rows in the Ticket Booking System application.
 */
@Controller
@RequestMapping("/admin/rows")
@RequiredArgsConstructor
@Slf4j
public class RowController {

    private final RowService rowService;

    private final MessageSource messageSource;

    /**
     * Handles GET requests to retrieve and display all rows.
     *
     * @param sectorId The ID of the sector to which the rows belong.
     * @param rowFilter The filter criteria for rows.
     * @param pageable The pagination information.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
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

    /**
     * Handles GET requests to show the form for creating a new row.
     *
     * @param arenaId The ID of the arena to which the row belongs.
     * @param sectorId The ID of the sector to which the row belongs.
     * @param rowCreateEditDto The row data transfer object.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/create")
    public String showCreateRowForm(@RequestParam("arenaId") Long arenaId,
                                    @RequestParam("sectorId") Long sectorId,
                                    @ModelAttribute("row") RowCreateEditDto rowCreateEditDto,
                                    Model model) {
        model.addAttribute("arenaId", arenaId);
        model.addAttribute("sectorId", sectorId);
        if (!model.containsAttribute("row")) {
            model.addAttribute("row", RowCreateEditDto.builder().build());
        }
        return "rows-jsp/create-row";
    }

    /**
     * Handles POST requests to create a new row.
     *
     * @param arenaId The ID of the arena to which the row belongs.
     * @param sectorId The ID of the sector to which the row belongs.
     * @param rowCreateEditDto The row data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @return The redirect URL.
     */
    @PostMapping("/create")
    public String createRow(@RequestParam("arenaId") Long arenaId,
                            @RequestParam("sectorId") Long sectorId,
                            @ModelAttribute @Validated RowCreateEditDto rowCreateEditDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("row", rowCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/rows/create?arenaId=" + arenaId + "&sectorId=" + sectorId;
            }
            log.info("Creating new row with details: {}", rowCreateEditDto);
            rowService.createRow(rowCreateEditDto, sectorId);
            return "redirect:/admin/rows?arenaId=" + arenaId + "&sectorId=" + sectorId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating sector: {}", e.getMessage());
            return handleCreateRowException(arenaId, sectorId, rowCreateEditDto, redirectAttributes, e);
        }
    }

    /**
     * Handles GET requests to show the form for updating an existing row.
     *
     * @param arenaId The ID of the arena to which the row belongs.
     * @param sectorId The ID of the sector to which the row belongs.
     * @param id The ID of the row to be updated.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
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

    /**
     * Handles POST requests to update an existing row.
     *
     * @param arenaId The ID of the arena to which the row belongs.
     * @param sectorId The ID of the sector to which the row belongs.
     * @param id The ID of the row to be updated.
     * @param rowCreateEditDto The row data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/update")
    public String updateRow(@RequestParam("arenaId") Long arenaId,
                            @RequestParam("sectorId") Long sectorId,
                            @PathVariable("id") Long id,
                            @ModelAttribute @Validated RowCreateEditDto rowCreateEditDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/rows/" + id + "/update?arenaId=" + arenaId + "&sectorId=" + sectorId;
            }
            log.info("Updating row {} with details: {}", id, rowCreateEditDto);
            rowService.updateRow(id, rowCreateEditDto, sectorId);
            return "redirect:/admin/rows?arenaId=" + arenaId + "&sectorId=" + sectorId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating row: {}", e.getMessage());
            return handleUpdateRowException(id, arenaId, sectorId, e, redirectAttributes);
        }
    }

    /**
     * Handles POST requests to delete an existing row.
     *
     * @param arenaId The ID of the arena to which the row belongs.
     * @param sectorId The ID of the sector to which the row belongs.
     * @param id The ID of the row to be deleted.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
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

            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("delete.row.fail", null, locale);

            validationResult.add(Error.of("delete.row.fail", errorMessage));

            model.addAttribute("errors", validationResult.getErrors());
            return "error-jsp/error-page";
        }
    }

    private String handleCreateRowException(Long arenaId,
                                            Long sectorId,
                                            RowCreateEditDto rowCreateEditDto,
                                            RedirectAttributes redirectAttributes,
                                            DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        redirectAttributes.addFlashAttribute("row", rowCreateEditDto);
        redirectAttributes.addFlashAttribute("errors", sqlExceptionResult.getErrors());
        return "redirect:/admin/rows/create?arenaId=" + arenaId + "&sectorId=" + sectorId;
    }

    private String handleUpdateRowException(Long id,
                                            Long arenaId,
                                            Long sectorId,
                                            DaoCrudException e,
                                            RedirectAttributes redirectAttributes) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        redirectAttributes.addFlashAttribute("errors", sqlExceptionResult.getErrors());
        return "redirect:/admin/rows/" + id + "/update?arenaId=" + arenaId + "&sectorId=" + sectorId;
    }

    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        Locale locale = getLocale();

        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_ROWS" -> {
                    String message = messageSource.getMessage("create_edit.row.fail.rows", null, locale);
                    sqlExceptionResult.add(Error.of("create_edit.row.fail", message));
                }
                case "ERROR_CHECK_SEATS" -> {
                    String message = messageSource.getMessage("create_edit.row.fail.seats", null, locale);
                    sqlExceptionResult.add(Error.of("create_edit.row.fail", message));
                }
                case "ERROR_CHECK_ROW_NUMBER" -> {
                    String message = messageSource.getMessage("create_edit.row.fail.row_number", null, locale);
                    sqlExceptionResult.add(Error.of("create_edit.row.fail", message));
                }
                default -> sqlExceptionResult.add(Error.of("create_edit.row.fail", errorMessage));
            }
        } else {
            String message = messageSource.getMessage("create_edit.fail.unknown", null, locale);
            sqlExceptionResult.add(Error.of("create_edit.row.fail", message));
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
