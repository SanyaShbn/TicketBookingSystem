package com.example.ticketbookingsystem.servlet.row_servlets;

import com.example.ticketbookingsystem.dto.RowDto;
import com.example.ticketbookingsystem.entity.Row;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.CreateUpdateEntityException;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.service.RowService;
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

@WebServlet("/admin/update-row")
public class UpdateRowServlet extends HttpServlet {
    private final RowService rowService = RowService.getInstance();
    private final SectorService sectorService = SectorService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToUpdateRowPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            RowDto rowDto = buildRowDto(request);
            rowService.updateRow(Long.parseLong(request.getParameter("id")), rowDto);
            redirectAfterSuccess(request, response);
        } catch (NumberFormatException e) {
            handleNumberFormatException(request, response);
        } catch (CreateUpdateEntityException e) {
            handleCreateUpdateSectorException(request, response, e);
        } catch (ValidationException e) {
            handleValidationException(request, response, e);
        }
    }

    private void forwardToUpdateRowPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Optional<Row> row = rowService.findById(Long.parseLong(id));
        row.ifPresent(value -> request.setAttribute("row", value));
        request.getRequestDispatcher(JspFilesResolver.getPath("/rows-jsp/update-row"))
                .forward(request, response);
    }

    private RowDto buildRowDto(HttpServletRequest request) throws NumberFormatException, IOException {
        int rowNumber = Integer.parseInt(request.getParameter("rowNumber"));
        int seatsNumb = Integer.parseInt(request.getParameter("seatsNumb"));
        Long sectorId = Long.valueOf(request.getParameter("sectorId"));
        Optional<Sector> sector = sectorService.findById(sectorId);

        return RowDto.builder()
                .rowNumber(rowNumber)
                .seatsNumb(seatsNumb)
                .sector(sector.orElseThrow(() -> new IOException("Sector not found")))
                .build();
    }

    private void redirectAfterSuccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/rows?arenaId=" +
                request.getParameter("arenaId") + "&sectorId=" + request.getParameter("sectorId"));
    }

    private void handleNumberFormatException(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.number.format",
                "Проверьте корректность ввода данных!"));
        request.setAttribute("errors", validationResult.getErrors());
        forwardToUpdateRowPage(request, response);
    }

    private void handleCreateUpdateSectorException(HttpServletRequest request, HttpServletResponse response,
                                                   CreateUpdateEntityException e) throws ServletException, IOException {
        ValidationResult sqlExceptionResult = new ValidationResult();
        specifySQLException(e.getMessage(), sqlExceptionResult);
        request.setAttribute("errors", sqlExceptionResult.getErrors());
        forwardToUpdateRowPage(request, response);
    }

    private void handleValidationException(HttpServletRequest request, HttpServletResponse response,
                                           ValidationException e) throws ServletException, IOException {
        request.setAttribute("errors", e.getErrors());
        forwardToUpdateRowPage(request, response);
    }
    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "ERROR_CHECK_SEATS" -> sqlExceptionResult.add(Error.of("create.row.fail",
                        "Ошибка! Суммарное количество мест рядов сектора не может превышать " +
                                "заданное маскимальное количество доступных мест в секторе. " +
                                "Проверьте корректность ввода данных"));
                case "ERROR_CHECK_ROW_NUMBER" -> sqlExceptionResult.add(Error.of("update.row.fail",
                        "Ошибка! Ряд с таким номером уже существует в данном секторе. " +
                                "Проверьте корректность ввода данных"));
                default -> sqlExceptionResult.add(Error.of("create.row.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("create.row.fail", "Unknown sql exception"));
        }
    }

    private String getErrorType(String errorMessage) {
        if (errorMessage.contains("ERROR_CHECK_SEATS")) {
            return "ERROR_CHECK_SEATS";
        } else if (errorMessage.contains("ERROR_CHECK_ROW_NUMBER")) {
            return "ERROR_CHECK_ROW_NUMBER";
        } else {
            return "UNKNOWN_ERROR";
        }
    }
}