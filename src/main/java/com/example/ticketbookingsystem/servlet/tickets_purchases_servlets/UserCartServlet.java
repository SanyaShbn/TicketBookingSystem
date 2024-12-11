package com.example.ticketbookingsystem.servlet.tickets_purchases_servlets;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.service.UserCartService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

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

        try{
            if ("clear".equals(action) || ticketId == null || ticketId.isEmpty()) {
                handleClearAction(user, response);
            }
            else {
                Long ticketIdLong = Long.parseLong(ticketId);
                UserCartDto userCartDto = buildUserCartDto(user, ticketIdLong);
                String status = ticketService.getTicketStatus(ticketIdLong);
                if (status != null) {
                    handleAction(action, status, userCartDto, response);
                }
                else {
                    sendErrorResponse(response, "Ticket not found");
                }
            }
        } catch (SQLException e) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("user.cart.items.error",
                    "Данный билет бронируется другим пользователем." +
                            " Просим прощения за доставленные неудобства"));
            request.setAttribute("errors", validationResult.getErrors());
            request.getRequestDispatcher(JspFilesResolver.getPath("/error-jsp/error-page"))
                    .forward(request, response);
        }
    }

    private void handleClearAction(User user, HttpServletResponse response) throws IOException, DaoCrudException {
        userCartService.clearUserCart(user.getId());
        sendSuccessResponse(response);
    }

    private UserCartDto buildUserCartDto(User user, Long ticketIdLong) {
        return UserCartDto.builder()
                .user(user)
                .ticket(ticketService.findById(ticketIdLong).get())
                .build();
    }

    private void handleAction(String action, String status, UserCartDto userCartDto, HttpServletResponse response)
            throws IOException, SQLException {
        switch (action) {
            case "add" -> {
                if ("AVAILABLE".equals(status)) {
                    userCartService.addItemToCart(userCartDto);
                    sendSuccessResponse(response);
                } else {
                    throw new SQLException();
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
