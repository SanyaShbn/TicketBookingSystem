package com.example.ticketbookingsystem.servlet.sectors_servlets;

import com.example.ticketbookingsystem.dto.SectorDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.CreateUpdateEntityException;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SectorService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/admin/create-sector")
public class CreateSectorServlet extends HttpServlet {
    private final SectorService sectorService = SectorService.getInstance();
    private final ArenaService arenaService = ArenaService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(JspFilesResolver.getPath("/sectors-jsp/create-sector"))
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            handleCreateSectorRequest(request, response);
        } catch (NumberFormatException e) {
            handleNumberFormatException(request, response);
        }catch (CreateUpdateEntityException e) {
            handleCreateUpdateSectorException(request, response, e);
        }catch (ValidationException e) {
            handleValidationException(request, response, e);
        }
    }

    private void handleCreateSectorRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SectorDto sectorDto = buildSectorDto(request);
        sectorService.createSector(sectorDto);
        response.sendRedirect(request.getContextPath() + "/admin/sectors?arenaId="
                              + request.getParameter("arenaId"));
    }

    private SectorDto buildSectorDto(HttpServletRequest request) throws IOException {
        String sectorName = request.getParameter("sectorName");
        Long arenaId = Long.valueOf(request.getParameter("arenaId"));
        Optional<Arena> arena = arenaService.findById(arenaId);
        int maxRowsNumb = Integer.parseInt(request.getParameter("maxRowsNumb"));
        int maxSeatsNumb = Integer.parseInt(request.getParameter("maxSeatsNumb"));

        return SectorDto.builder()
                .sectorName(sectorName)
                .arena(arena.orElseThrow(() -> new IOException("Arena not found")))
                .maxRowsNumb(maxRowsNumb)
                .maxSeatsNumb(maxSeatsNumb)
                .build();
    }

    private void handleNumberFormatException(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ValidationResult numberFormatValidationResult = new ValidationResult();
        numberFormatValidationResult.add(Error.of("invalid.number.format",
                "Проверьте корректность ввода данных!"));
        request.setAttribute("errors", numberFormatValidationResult.getErrors());
        doGet(request, response);
    }

    private void handleValidationException(HttpServletRequest request, HttpServletResponse response,
                                           ValidationException e)
            throws ServletException, IOException {
        request.setAttribute("errors", e.getErrors());
        doGet(request, response);
    }

    private void handleCreateUpdateSectorException(HttpServletRequest request, HttpServletResponse response,
                                                   CreateUpdateEntityException e)
            throws ServletException, IOException {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        request.setAttribute("errors", sqlExceptionResult.getErrors());
        doGet(request, response);
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
            return "ERROR_CHECK_CAPACITY";}
        else {
            return "UNKNOWN_ERROR";
        }
    }

}
