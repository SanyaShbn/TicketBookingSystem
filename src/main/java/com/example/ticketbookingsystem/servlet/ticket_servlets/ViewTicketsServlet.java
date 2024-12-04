package com.example.ticketbookingsystem.servlet.ticket_servlets;

import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/tickets")
public class ViewTicketsServlet extends HttpServlet {
    private final TicketService ticketService = TicketService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Long eventId = Long.valueOf(req.getParameter("eventId"));

        req.setAttribute("tickets", ticketService.findAllByEventId(eventId));
        req.getRequestDispatcher(JspFilesResolver.getPath("/tickets-jsp/tickets")).forward(req, resp);
    }
}
