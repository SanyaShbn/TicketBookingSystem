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
@RequestMapping("/admin/sectors")
@RequiredArgsConstructor
@Slf4j
public class SectorController {

    private final SectorService sectorService;

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
            validationResult.add(Error.of("delete.sector.fail",
                    "Ошибка! Данные о выбранном секторе арены нельзя удалить. На него уже добавлены билеты " +
                            "в разделе 'Предстоящие спортивные события'"));
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
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_SECTOR_NAME" -> sqlExceptionResult.add(Error.of("create_edit.sector.fail",
                        "Ошибка! Запись о секторе с заданным наименованием уже существует. " +
                        "Проверьте корректность ввода данных"));
                case "ERROR_CHECK_CAPACITY" -> sqlExceptionResult.add(Error.of("create_edit.sector.fail",
                        "Ошибка! Общее максимально возможное количество мест в секторах для " +
                        "заданной арены превышает ее вместимость. Проверьте корреткность ввода данных"));
                case "ERROR_CHECK_ROWS" -> sqlExceptionResult.add(Error.of("create_edit.sector.fail",
                        "Ошибка! Заданное максимальное значение рядов сектора меньше, " +
                                "чем количество уже созданных секторов. " +
                                "Проверьте корректность ввода данных"));
                case "ERROR_CHECK_SEATS" -> sqlExceptionResult.add(Error.of("create_edit.sector.fail",
                        "Ошибка! Заданное максимальное значение мест сектора меньше, " +
                                "чем количество мест уже созданных рядов для сектора. " +
                                "Проверьте корректность ввода данных"));
                default -> sqlExceptionResult.add(Error.of("create_edit.sector.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("create_edit.sector.fail", "Unknown sql exception"));
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
