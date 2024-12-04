package com.example.ticketbookingsystem.servlet.sport_event_servlets;

import com.example.ticketbookingsystem.dto.SportEventDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@WebServlet("/update-sport-event")
public class UpdateSportEventServlet extends HttpServlet {
    private final SportEventService sportEventService = SportEventService.getInstance();
    private final ArenaService arenaService = ArenaService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Optional<SportEvent> sportEvent = sportEventService.findById(Long.parseLong(id));
        request.setAttribute("sport_event", sportEvent.orElse(null));
        request.getRequestDispatcher(JspFilesResolver.getPath("/sport-events-jsp/update-sport-event"))
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String id = request.getParameter("id");
        String eventName = request.getParameter("eventName");
        LocalDateTime eventDateTime = Timestamp.valueOf(request.getParameter("eventDateTime")).toLocalDateTime();

        Long arenaId = Long.valueOf(request.getParameter("arenaId"));
        Optional<Arena> arena = arenaService.findById(arenaId);

        SportEventDto sportEventDto = SportEventDto.builder()
                .eventName(eventName)
                .eventDateTime(eventDateTime)
                .arena(arena.orElseThrow(() -> new IOException("Arena not found")))
                .build();

        sportEventService.updateSportEvent(Long.parseLong(id), sportEventDto);
        response.sendRedirect(request.getContextPath() + "/sport-events");
    }
}
