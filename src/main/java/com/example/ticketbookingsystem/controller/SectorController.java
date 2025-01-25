package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.SectorCreateEditDto;
import com.example.ticketbookingsystem.dto.SectorReadDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.SectorService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/sectors")
@RequiredArgsConstructor
@Slf4j
public class SectorController {

    private final SectorService sectorService;

    @GetMapping
    public String findAllSectors(@RequestParam("arenaId") Long arenaId,
                                Model model) {
        List<SectorReadDto> sectorReadDtoList = sectorService.findAllByArenaId(arenaId);
        model.addAttribute("sectors", sectorReadDtoList);
        return JspFilesResolver.getPath("/sectors-jsp/sectors");
    }

    @GetMapping("/create")
    public String showCreateSectorForm(@RequestParam("arenaId") Long arenaId, Model model) {
        model.addAttribute("arenaId", arenaId);
        return JspFilesResolver.getPath("/sectors-jsp/create-sector");
    }

    @PostMapping("/create")
    public String createSector(@RequestParam("arenaId") Long arenaId,
                               @ModelAttribute SectorCreateEditDto sectorCreateEditDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("sector", sectorCreateEditDto);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                redirectAttributes.addAttribute("arenaId", arenaId);
                return "redirect:/admin/sectors/create";
            }
            log.info("Creating new sector with details: {}", sectorCreateEditDto);
            sectorService.createSector(sectorCreateEditDto, arenaId);
            return "redirect:/admin/sectors?arenaId=" + arenaId;
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            return handleNumberFormatException(model);
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while creating sector: {}", e.getMessage());
            return handleCreateSectorException(model, e);
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
            return JspFilesResolver.getPath("/sectors-jsp/update-sector");
        }
        return "redirect:/admin/sectors?arenaId=" + arenaId;
    }

    @PostMapping("/{id}/update")
    public String updateSector(@RequestParam("arenaId") Long arenaId,
                               @PathVariable("id") Long id,
                               @ModelAttribute SectorCreateEditDto sectorCreateEditDto,
                               Model model) {
        try {
            log.info("Updating sector {} with details: {}", id, sectorCreateEditDto);
            sectorService.updateSector(id, sectorCreateEditDto, arenaId);
            return "redirect:/admin/sectors?arenaId=" + arenaId;
        } catch (NumberFormatException e) {
            log.error("Number format exception occurred: {}", e.getMessage());
            return handleNumberFormatException(model);
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while updating sector: {}", e.getMessage());
            return handleUpdateSectorException(model, e);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteArena(@RequestParam("arenaId") Long arenaId,
                              @PathVariable("id") Long id,
                              Model model) {
        try {
            log.info("Deleting sector with id: {}", id);
            sectorService.deleteSector(id);
            return "redirect:/admin/sectors?arenaId=" + arenaId;
        } catch (DaoCrudException e) {
            log.error("CRUD exception occurred while trying to delete sector: {}", e.getMessage());
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.row.fail",
                    "Ошибка! Данные о выбранном секторе арены нельзя удалить. На него уже добавлены билеты " +
                            "в разделе 'Предстоящие спортивные события'"));
            model.addAttribute("errors", validationResult.getErrors());
            return JspFilesResolver.getPath("/error-jsp/error-page");
        }
    }

    private String handleNumberFormatException(Model model) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.number.format",
                "Проверьте корректность ввода данных!"));
        model.addAttribute("errors", validationResult.getErrors());
        return JspFilesResolver.getPath("/sectors-jsp/create-sector");
    }

    private String handleCreateSectorException(Model model, DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        model.addAttribute("errors", sqlExceptionResult.getErrors());
        return JspFilesResolver.getPath("/sectors-jsp/create-sector");
    }

    private String handleUpdateSectorException(Model model, DaoCrudException e) {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        model.addAttribute("errors", sqlExceptionResult.getErrors());
        return JspFilesResolver.getPath("/sectors-jsp/update-sector");
    }

    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_SECTOR_NAME" -> sqlExceptionResult.add(Error.of("create.sector.fail",
                        "Ошибка! Запись о секторе с заданным наименованием уже существует. " +
                        "Проверьте корректность ввода данных"));
                case "ERROR_CHECK_CAPACITY" -> sqlExceptionResult.add(Error.of("create.sector.fail",
                        "Ошибка! Общее максимально возможное количество мест в секторах для " +
                        "заданной арены превышает ее вместимость. Проверьте корреткность ввода данных"));
                case "ERROR_CHECK_ROWS" -> sqlExceptionResult.add(Error.of("create.row.fail",
                        "Ошибка! Заданное максимальное значение рядов сектора меньше, " +
                                "чем количество уже созданных секторов. " +
                                "Проверьте корректность ввода данных"));
                case "ERROR_CHECK_SEATS" -> sqlExceptionResult.add(Error.of("create.row.fail",
                        "Ошибка! Заданное максимальное значение мест сектора меньше, " +
                                "чем количество мест уже созданных рядов для сектора. " +
                                "Проверьте корректность ввода данных"));
                default -> sqlExceptionResult.add(Error.of("create.row.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("create.row.fail", "Unknown sql exception"));
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
