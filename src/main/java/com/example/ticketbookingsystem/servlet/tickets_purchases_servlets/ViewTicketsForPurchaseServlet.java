package com.example.ticketbookingsystem.servlet.tickets_purchases_servlets;

import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.service.SeatService;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/view_available_tickets")
public class ViewTicketsForPurchaseServlet extends HttpServlet {
    private final TicketService ticketService = TicketService.getInstance();
    private final SeatService seatService = SeatService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Long eventId = Long.valueOf(req.getParameter("id"));
        List<Seat> seats = seatService.findAllByEventId(eventId);
        List<Ticket> tickets = ticketService.findAllByEventId(eventId);

        req.setAttribute("seats", seats);
        req.setAttribute("tickets", tickets);

        req.getRequestDispatcher(JspFilesResolver
                .getPath("/tickets-purchases-jsp/view-available-tickets")).forward(req, resp);
    }
}
