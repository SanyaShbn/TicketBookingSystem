package com.example.ticketbookingsystem.servlet.sport_event_servlets;

import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/sport_events")
public class ViewSportEventsServlet extends HttpServlet {
    private final SportEventService sportEventService = SportEventService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        req.setAttribute("sport_events", sportEventService.findAll());
        req.getRequestDispatcher(JspFilesResolver.getPath("/sport-events-jsp/sport_events")).forward(req, resp);
    }
}
