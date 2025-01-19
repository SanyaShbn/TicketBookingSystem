package com.example.ticketbookingsystem.servlet.ticket_servlets;

import com.example.ticketbookingsystem.dto.TicketDto;
import com.example.ticketbookingsystem.entity.Seat;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.entity.TicketStatus;
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
 * Servlet for managing requests to create a ticket.
 */
@WebServlet("/admin/create-ticket")
public class CreateTicketServlet extends HttpServlet {
    private final TicketService ticketService = TicketService.getInstance();
    private final SportEventService sportEventService = SportEventService.getInstance();
    private final SeatService seatService = SeatService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String eventId = request.getParameter("eventId");
        List<Seat> seats = seatService.findByEventIdWithNoTickets(Long.valueOf(eventId));
        request.setAttribute("seats", seats);
        request.getRequestDispatcher(JspFilesResolver.getPath("/tickets-jsp/create-ticket"))
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            TicketStatus status = TicketStatus.valueOf("AVAILABLE");
            BigDecimal price = new BigDecimal(req.getParameter("price"));

            Long sportEventId = Long.valueOf(req.getParameter("eventId"));
            Optional<SportEvent> sportEvent = sportEventService.findById(sportEventId);
            Long seatId = Long.valueOf(req.getParameter("seat"));
            Optional<Seat> seat = seatService.findById(seatId);

            TicketDto ticketDto = TicketDto.builder()
                    .status(status)
                    .price(price)
                    .sportEvent(sportEvent.orElseThrow(() -> new IOException("Event not found")))
                    .seat(seat.orElseThrow(() -> new IOException("Seat not found")))
                    .build();

            ticketService.createTicket(ticketDto);
            resp.sendRedirect(req.getContextPath() + "/admin/tickets?eventId=" + req.getParameter("eventId"));
        }catch (NumberFormatException e) {
            ValidationResult numberFormatValidationResult = new ValidationResult();
            numberFormatValidationResult.add(Error.of("invalid.number.format",
                    "Проверьте корректность ввода данных!"));
            req.setAttribute("errors", numberFormatValidationResult.getErrors());
            doGet(req, resp);
        }
    }
}
