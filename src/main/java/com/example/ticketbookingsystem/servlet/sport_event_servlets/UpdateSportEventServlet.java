package com.example.ticketbookingsystem.servlet.sport_event_servlets;

import com.example.ticketbookingsystem.dto.SportEventDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
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
 * Servlet for managing requests to update a sporting event.
 */
@WebServlet("/admin/update-sport-event")
public class UpdateSportEventServlet extends HttpServlet {
    private final SportEventService sportEventService = SportEventService.getInstance();
    private final ArenaService arenaService = ArenaService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Optional<SportEvent> sportEvent = sportEventService.findById(Long.parseLong(id));
        request.setAttribute("sport_event", sportEvent.orElse(null));
        List<Arena> arenas = arenaService.findAll();
        request.setAttribute("arenas", arenas);
        request.getRequestDispatcher(JspFilesResolver.getPath("/sport-events-jsp/update-sport-event"))
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String id = request.getParameter("id");
            String eventName = request.getParameter("eventName");

            String eventDateTimeString = request.getParameter("eventDateTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime eventDateTime = LocalDateTime.parse(eventDateTimeString, formatter);

            Long arenaId = Long.valueOf(request.getParameter("arena"));
            Optional<Arena> arena = arenaService.findById(arenaId);

            SportEventDto sportEventDto = SportEventDto.builder()
                    .eventName(eventName)
                    .eventDateTime(eventDateTime)
                    .arena(arena.orElseThrow(() -> new IOException("Arena not found")))
                    .build();

            sportEventService.updateSportEvent(Long.parseLong(id), sportEventDto);
            response.sendRedirect(request.getContextPath() + "/admin/sport_events");
        }catch (DaoCrudException e) {
            ValidationResult validationResult = new ValidationResult();
            specifySQLException(e.getMessage(), validationResult);
            request.setAttribute("errors", validationResult.getErrors());
            doGet(request, response);
        }
    }
    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
        if (errorMessage != null) {
            switch (getErrorType(errorMessage)) {
                case "EVENT_TIME_EXCEPTION" -> sqlExceptionResult.add(Error.of("update.event.error",
                        "Создаваемое событие не может пересекаться по времени с уже запланированными событиями" +
                        " для выбранной арены (минимальный интервал для одной даты - 3 часа). " +
                        "Проверьте корректность ввода данных!"));
                case "EVENT_REFERENCE_EXCEPTION" -> sqlExceptionResult.add(Error.of("update.event.error",
                        "Выбранное событие нельзя редактировать, на него уже доступна продажа билетов. " +
                        "Если хотите выполнить редактирование, необходимо удалить всю информацию о продаваемых билетах"));
                default -> sqlExceptionResult.add(Error.of("create.row.fail", errorMessage));
            }
        } else {
            sqlExceptionResult.add(Error.of("update.row.fail", "Unknown sql exception"));
        }
    }
    private String getErrorType(String errorMessage) {
        if (errorMessage.contains("EVENT_TIME_EXCEPTION")) {
            return "EVENT_TIME_EXCEPTION";
        } else if (errorMessage.contains("EVENT_REFERENCE_EXCEPTION")) {
            return "EVENT_REFERENCE_EXCEPTION";
        } else {
            return "UNKNOWN_ERROR";
        }
    }
}
