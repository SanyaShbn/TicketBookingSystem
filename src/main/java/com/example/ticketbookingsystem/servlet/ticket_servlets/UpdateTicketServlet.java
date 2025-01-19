package com.example.ticketbookingsystem.servlet.ticket_servlets;

import com.example.ticketbookingsystem.dto.TicketDto;
import com.example.ticketbookingsystem.entity.*;
import com.example.ticketbookingsystem.service.SeatService;
import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servlet for managing requests to update a ticket.
 */
@WebServlet("/admin/update-ticket")
public class UpdateTicketServlet extends HttpServlet {
    private final TicketService ticketService = TicketService.getInstance();
    private final SportEventService sportEventService = SportEventService.getInstance();
    private final SeatService seatService = SeatService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Optional<Ticket> ticket = ticketService.findById(Long.parseLong(id));
        request.setAttribute("ticket", ticket.orElse(null));

        String eventId = request.getParameter("eventId");
        List<Seat> seats = seatService.findAllByEventIdWhenUpdate(Long.valueOf(eventId), ticket.get().getSeat().getId());
        request.setAttribute("seats", seats);

        request.getRequestDispatcher(JspFilesResolver.getPath("/tickets-jsp/update-ticket"))
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String id = request.getParameter("id");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            TicketStatus status = TicketStatus.valueOf("AVAILABLE");
            Long sportEventId = Long.valueOf(request.getParameter("eventId"));
            Optional<SportEvent> sportEvent = sportEventService.findById(sportEventId);
            Long seatId = Long.valueOf(request.getParameter("seat"));
            Optional<Seat> seat = seatService.findById(seatId);

            TicketDto ticketDto = TicketDto.builder()
                    .status(status)
                    .price(price)
                    .sportEvent(sportEvent.orElseThrow(() -> new IOException("Event not found")))
                    .seat(seat.orElseThrow(() -> new IOException("Seat not found")))
                    .build();

            ticketService.updateTicket(Long.parseLong(id), ticketDto);
            response.sendRedirect(request.getContextPath() + "/admin/tickets?eventId=" + request.getParameter("eventId"));
        }catch (NumberFormatException e) {
            ValidationResult numberFormatValidationResult = new ValidationResult();
            numberFormatValidationResult.add(Error.of("invalid.number.format",
                    "Проверьте корректность ввода данных!"));
            request.setAttribute("errors", numberFormatValidationResult.getErrors());
            doGet(request, response);
        }
    }
}
