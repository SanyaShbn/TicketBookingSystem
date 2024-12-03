package com.example.ticketbookingsystem.servlet.sectors_servlets;

import com.example.ticketbookingsystem.dto.SectorDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.Sector;
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

@WebServlet("/update-sector")
public class UpdateSectorServlet extends HttpServlet {
    private final SectorService sectorService = SectorService.getInstance();
    private final ArenaService arenaService = ArenaService.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Optional<Sector> sector = sectorService.findById(Long.parseLong(id));
        request.setAttribute("sector", sector.get());
        request.getRequestDispatcher(
                JspFilesResolver.getPath("/sectors-jsp/update-sector")).forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try{
            String id = request.getParameter("id");
            String sectorName = request.getParameter("sectorName");
            Long arenaId = Long.valueOf(request.getParameter("arenaId"));
            Optional<Arena> arena = arenaService.findById(arenaId);
            int maxRowsNumb = Integer.parseInt(request.getParameter("maxRowsNumb"));
//            int availableRowsNumb = Integer.parseInt(request.getParameter("availableRowsNumb"));
            int maxSeatsNumb = Integer.parseInt(request.getParameter("maxSeatsNumb"));
//            int availableSeatsNumb = Integer.parseInt(request.getParameter("availableSeatsNumb"));

            SectorDto sectorDto = SectorDto.builder()
                    .sectorName(sectorName)
                    .arena(arena.get())
                    .maxRowsNumb(maxRowsNumb)
                    .maxSeatsNumb(maxSeatsNumb)
                    .build();

            sectorService.updateSector(Long.parseLong(id), sectorDto);

            response.sendRedirect(request.getContextPath() + "/sectors?arenaId=" + arenaId);
        }catch (NumberFormatException e){
            ValidationResult numberFormatValidationResult = new ValidationResult();
            numberFormatValidationResult.add(Error.of("invalid.number.format",
                    "Проверьте корректность ввода данных!"));
            request.setAttribute("errors", numberFormatValidationResult.getErrors());
            doGet(request, response);
        }
        catch (ValidationException e){
            request.setAttribute("errors", e.getErrors());
            doGet(request, response);
        }
    }
}
