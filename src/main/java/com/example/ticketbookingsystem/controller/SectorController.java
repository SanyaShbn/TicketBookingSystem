package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.sector_dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.sector_dto.SectorFilter;
import com.example.ticketbookingsystem.dto.sector_dto.SectorReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.SectorService;
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
 * Controller class for managing sectors in the Ticket Booking System application.
 */
@Controller
@RequestMapping("/admin/sectors")
@RequiredArgsConstructor
@Slf4j
public class SectorController {

    private final SectorService sectorService;

    private final MessageSource messageSource;

    /**
     * Handles GET requests to retrieve and display all sectors.
     *
     * @param arenaId The ID of the arena to which the sectors belong.
     * @param sectorFilter The filter criteria for sectors.
     * @param pageable The pagination information.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping
    public String findAllSectors(@RequestParam("arenaId") Long arenaId,
                                 SectorFilter sectorFilter,
                                 Pageable pageable,
                                 Model model) {
        Page<SectorReadDto> sectorsPage = sectorService.findAll(arenaId, sectorFilter, pageable);
        model.addAttribute("filter", sectorFilter);
        model.addAttribute("sectors", PageResponse.of(sectorsPage));
        return "sectors-jsp/sectors";
    }

    /**
     * Handles GET requests to show the form for creating a new sector.
     *
     * @param arenaId The ID of the arena to which the sector belongs.
     * @param sectorCreateEditDto The sector data transfer object.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/create")
    public String showCreateSectorForm(@RequestParam("arenaId") Long arenaId,
                                       @ModelAttribute("sector") SectorCreateEditDto sectorCreateEditDto,
                                       Model model) {
        model.addAttribute("arenaId", arenaId);
        if (!model.containsAttribute("sector")) {
            model.addAttribute("sector", SectorCreateEditDto.builder().build());
        }
        return "sectors-jsp/create-sector";
    }

    /**
     * Handles POST requests to create a new sector.
     *
     * @param arenaId The ID of the arena to which the sector belongs.
     * @param sectorCreateEditDto The sector data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @return The redirect URL.
     */
    @PostMapping("/create")
    public String createSector(@RequestParam("arenaId") Long arenaId,
                               @ModelAttribute @Validated SectorCreateEditDto sectorCreateEditDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("sector", sectorCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/sectors/create?arenaId=" + arenaId;
            }
            log.info("Creating new sector with details: {}", sectorCreateEditDto);
            sectorService.createSector(sectorCreateEditDto, arenaId);
            return "redirect:/admin/sectors?arenaId=" + arenaId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating sector: {}", e.getMessage());
            return handleCreateSectorException(arenaId, sectorCreateEditDto, redirectAttributes, e);
        }
    }

    /**
     * Handles GET requests to show the form for updating an existing sector.
     *
     * @param arenaId The ID of the arena to which the sector belongs.
     * @param id The ID of the sector to be updated.
     * @param model The model to hold attributes.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/{id}/update")
    public String showUpdateSectorForm(@RequestParam("arenaId") Long arenaId,
                                       @PathVariable Long id,
                                       Model model) {
        Optional<SectorReadDto> sector = sectorService.findById(id);
        if (sector.isPresent()) {
            model.addAttribute("sector", sector.get());
            model.addAttribute("arenaId", arenaId);
            return "sectors-jsp/update-sector";
        }
        return "redirect:/admin/sectors?arenaId=" + arenaId;
    }

    /**
     * Handles POST requests to update an existing sector.
     *
     * @param arenaId The ID of the arena to which the sector belongs.
     * @param id The ID of the sector to be updated.
     * @param sectorCreateEditDto The sector data transfer object.
     * @param bindingResult The binding result for validation.
     * @param redirectAttributes The redirect attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/update")
    public String updateSector(@RequestParam("arenaId") Long arenaId,
                               @PathVariable("id") Long id,
                               @ModelAttribute @Validated SectorCreateEditDto sectorCreateEditDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                return "redirect:/admin/sectors/" + id + "/update?arenaId=" + arenaId;
            }
            log.info("Updating sector {} with details: {}", id, sectorCreateEditDto);
            sectorService.updateSector(id, sectorCreateEditDto, arenaId);
            return "redirect:/admin/sectors?arenaId=" + arenaId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating sector: {}", e.getMessage());
            return handleUpdateSectorException(id, arenaId, e, redirectAttributes);
        }
    }

    /**
     * Handles POST requests to delete an existing sector.
     *
     * @param arenaId The ID of the arena to which the sector belongs.
     * @param id The ID of the sector to be deleted.
     * @param model The model to hold attributes.
     * @return The redirect URL.
     */
    @PostMapping("/{id}/delete")
    public String deleteSector(@RequestParam("arenaId") Long arenaId,
                              @PathVariable("id") Long id,
                              Model model) {
        try {
            log.info("Deleting sector with id: {}", id);
            sectorService.deleteSector(id);
            return "redirect:/admin/sectors?arenaId=" + arenaId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while trying to delete sector: {}", e.getMessage());
            ValidationResult validationResult = new ValidationResult();

            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("delete.sector.fail", null, locale);

            validationResult.add(Error.of("delete.sector.fail", errorMessage));
            model.addAttribute("errors", validationResult.getErrors());
            return "error-jsp/error-page";
        }
    }

    private String handleCreateSectorException(Long arenaId,
                                               SectorCreateEditDto sectorCreateEditDto,
                                               RedirectAttributes redirectAttributes,
                                               DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        redirectAttributes.addFlashAttribute("sector", sectorCreateEditDto);
        redirectAttributes.addFlashAttribute("errors", sqlExceptionResult.getErrors());
        return "redirect:/admin/sectors/create?arenaId=" + arenaId;
    }

    private String handleUpdateSectorException(Long id,
                                               Long arenaId,
                                               DaoCrudException e,
                                               RedirectAttributes redirectAttributes) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        redirectAttributes.addFlashAttribute("errors", sqlExceptionResult.getErrors());
        return "redirect:/admin/sectors/" + id + "/update?arenaId=" + arenaId;
    }

    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        Locale locale = getLocale();

        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_SECTOR_NAME" -> {
                    String message = messageSource.getMessage("create_edit.sector.fail.name", null, locale);
                    sqlExceptionResult.add(Error.of("create_edit.sector.fail", message));
                }
                case "ERROR_CHECK_CAPACITY" -> {
                    String message = messageSource.getMessage("create_edit.sector.fail.capacity", null, locale);
                    sqlExceptionResult.add(Error.of("create_edit.sector.fail", message));
                }
                case "ERROR_CHECK_ROWS" -> {
                    String message = messageSource.getMessage("create_edit.sector.fail.rows", null, locale);
                    sqlExceptionResult.add(Error.of("create_edit.sector.fail", message));
                }
                case "ERROR_CHECK_SEATS" -> {
                    String message = messageSource.getMessage("create_edit.sector.fail.seats", null, locale);
                    sqlExceptionResult.add(Error.of("create_edit.sector.fail", message));
                }
                default -> sqlExceptionResult.add(Error.of("create_edit.sector.fail", errorMessage));
            }
        } else {
            String message = messageSource.getMessage("create_edit.fail.unknown", null, locale);
            sqlExceptionResult.add(Error.of("create_edit.sector.fail", message));
        }
    }

    private String getErrorType(String errorMessage) {
        if (errorMessage.contains("ERROR_CHECK_SECTOR_NAME")) {
            return "ERROR_CHECK_SECTOR_NAME";
        } else if (errorMessage.contains("ERROR_CHECK_CAPACITY")) {
            return "ERROR_CHECK_CAPACITY";
        } else if (errorMessage.contains("ERROR_CHECK_ROWS")) {
            return "ERROR_CHECK_ROWS";
        } else if (errorMessage.contains("ERROR_CHECK_SEATS")) {
            return "ERROR_CHECK_SEATS";}
        else {
            return "UNKNOWN_ERROR";
        }
    }
}
