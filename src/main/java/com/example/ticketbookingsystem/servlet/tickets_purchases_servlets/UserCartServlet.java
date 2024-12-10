package com.example.ticketbookingsystem.servlet.tickets_purchases_servlets;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.service.UserCartService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/user_cart")
public class UserCartServlet extends HttpServlet {

    private final UserCartService userCartService = UserCartService.getInstance();
    private final TicketService ticketService = TicketService.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(JspFilesResolver.getPath("/tickets-purchases-jsp/view-available-tickets"))
                .forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        String ticketId = request.getParameter("ticketId");
        String action = request.getParameter("action");

        try {
            if ("clear".equals(action) || ticketId == null || ticketId.isEmpty()) {
                userCartService.clearUserCart(user.getId());
                sendSuccessResponse(response);
                return;
            }

            Long ticketIdLong = Long.parseLong(ticketId);

            UserCartDto userCartDto = UserCartDto.builder()
                    .user(user)
                    .ticket(ticketService.findById(Long.valueOf(ticketId)).get())
                    .build();

            String status = ticketService.getTicketStatus(ticketIdLong);
            if (status != null){
                switch (action) {
                    case "add" -> {
                        if ("AVAILABLE".equals(status)) {
                            userCartService.addItemToCart(userCartDto);
                            sendSuccessResponse(response);
                        } else {
                            sendErrorResponse(response, "Invalid ticket status");
                        }
                    }
                    case "remove" -> {
                        if ("RESERVED".equals(status)) {
                            userCartService.removeItemFromCart(userCartDto);
                            sendSuccessResponse(response);
                        } else {
                            sendErrorResponse(response, "Invalid ticket status");
                        }
                    }
                    default -> sendErrorResponse(response, "Invalid action");
                }
            } else {
                sendErrorResponse(response, "Ticket not found");
            }
        } catch (DaoCrudException e) {
            sendErrorResponse(response, "Database error");
        }
    }

    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true}");
    }
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
    }
}
