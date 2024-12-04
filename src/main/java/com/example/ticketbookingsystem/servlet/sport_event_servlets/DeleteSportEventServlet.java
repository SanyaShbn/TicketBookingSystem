package com.example.ticketbookingsystem.servlet.sport_event_servlets;

import com.example.ticketbookingsystem.service.SportEventService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete-sport-event")
public class DeleteSportEventServlet extends HttpServlet {
    private final SportEventService sportEventService = SportEventService.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        sportEventService.deleteSportEvent(Long.parseLong(id));
        response.sendRedirect(request.getContextPath() + "/sport-events");
    }
}
