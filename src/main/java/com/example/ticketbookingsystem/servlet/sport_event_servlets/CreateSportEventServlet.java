package com.example.ticketbookingsystem.servlet.sport_event_servlets;

import com.example.ticketbookingsystem.dto.SportEventDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Servlet for managing requests to create a sporting event.
 */
@WebServlet("/admin/create-sport-event")
public class CreateSportEventServlet extends HttpServlet {
    private final SportEventService sportEventService = SportEventService.getInstance();
    private final ArenaService arenaService = ArenaService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Arena> arenas = arenaService.findAll();
        request.setAttribute("arenas", arenas);
        request.getRequestDispatcher(JspFilesResolver.getPath("/sport-events-jsp/create-sport-event"))
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            String eventName = req.getParameter("eventName");

            String eventDateTimeString = req.getParameter("eventDateTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime eventDateTime = LocalDateTime.parse(eventDateTimeString, formatter);

            Long arenaId = Long.valueOf(req.getParameter("arena"));
            Optional<Arena> arena = arenaService.findById(arenaId);

            SportEventDto sportEventDto = SportEventDto.builder()
                    .eventName(eventName)
                    .eventDateTime(eventDateTime)
                    .arena(arena.orElseThrow(() -> new IOException("Arena not found")))
                    .build();
            sportEventService.createSportEvent(sportEventDto);
            resp.sendRedirect(req.getContextPath() + "/admin/sport_events");
        }catch (DaoCrudException e) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("create.event.error",
                    "Создаваемое событие не может пересекаться по времени с уже запланированными событиями" +
                    " для выбранной арены (минимальный интервал для одной даты - 3 часа). " +
                    "Проверьте корректность ввода данных!"));
            req.setAttribute("errors", validationResult.getErrors());
            doGet(req, resp);
        }
    }
}
