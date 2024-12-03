package com.example.ticketbookingsystem.servlet.sectors_servlets;

import com.example.ticketbookingsystem.dto.SectorDto;
import com.example.ticketbookingsystem.entity.Arena;
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

@WebServlet("/create-sector")
public class CreateSectorServlet extends HttpServlet {
    private final SectorService sectorService = SectorService.getInstance();
    private final ArenaService arenaService = ArenaService.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(
                JspFilesResolver.getPath("/sectors-jsp/create-sector")).forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            String sectorName = req.getParameter("sectorName");
            Long arenaId = Long.valueOf(req.getParameter("arenaId"));
            Optional<Arena> arena = arenaService.findById(arenaId);
            int maxRowsNumb = Integer.parseInt(req.getParameter("maxRowsNumb"));
//            int availableRowsNumb = Integer.parseInt(req.getParameter("availableRowsNumb"));
            int maxSeatsNumb = Integer.parseInt(req.getParameter("maxSeatsNumb"));
//            int availableSeatsNumb = Integer.parseInt(req.getParameter("availableSeatsNumb"));

            SectorDto sectorDto = SectorDto.builder()
                    .sectorName(sectorName)
                    .arena(arena.get())
                    .maxRowsNumb(maxRowsNumb)
                    .maxSeatsNumb(maxSeatsNumb)
                    .build();

            sectorService.createSector(sectorDto);

            resp.sendRedirect(req.getContextPath() + "/sectors?arenaId=" + arenaId);
        }catch (NumberFormatException e){
            ValidationResult numberFormatValidationResult = new ValidationResult();
            numberFormatValidationResult.add(Error.of("invalid.number.format",
                    "Проверьте корректность ввода данных!"));
            req.setAttribute("errors", numberFormatValidationResult.getErrors());
            doGet(req, resp);
        }
        catch (ValidationException e){
            req.setAttribute("errors", e.getErrors());
            doGet(req, resp);
        }
    }
}
