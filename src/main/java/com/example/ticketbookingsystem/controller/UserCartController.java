//package com.example.ticketbookingsystem.controller;
//
//import com.example.ticketbookingsystem.dto.UserCartDto;
//import com.example.ticketbookingsystem.dto.UserDto;
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.service.TicketService;
//import com.example.ticketbookingsystem.service.UserCartService;
//import com.example.ticketbookingsystem.utils.AuthenticationUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.Optional;
//
///**
// * Controller class for managing user cart in the Ticket Booking System application.
// */
//@Controller
//@RequestMapping("/user_cart")
//@RequiredArgsConstructor
//public class UserCartController {
//
//    private final UserCartService userCartService;
//
//    private final TicketService ticketService;
//
//    private final AuthenticationUtil authenticationUtil;
//
//    /**
//     * Handles GET requests to show the user cart page.
//     *
//     * @return The name of the view to be rendered.
//     */
//    @GetMapping
//    public String getUserCartPage() {
//        return "tickets-purchases-jsp/view-available-tickets";
//    }
//
//    /**
//     * Handles POST requests to manage user cart actions such as add, remove, or clear.
//     *
//     * @param request The HTTP request containing parameters.
//     * @param response The HTTP response.
//     * @throws IOException If an input or output exception occurred.
//     * @throws DaoCrudException If a CRUD exception occurred.
//     */
//    @PostMapping
//    @ResponseBody
//    public void handleUserCartRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, DaoCrudException {
//        Optional<UserDto> user = authenticationUtil.getAuthenticatedUser();
//        if (user.isEmpty()) {
//            sendErrorResponse(response, "User not authenticated");
//            return;
//        }
//        String ticketId = request.getParameter("ticketId");
//        String action = request.getParameter("action");
//
//        if ("clear".equals(action) || ticketId == null || ticketId.isEmpty()) {
//            handleClearAction(user.get(), response);
//        } else {
//            Long ticketIdLong = Long.parseLong(ticketId);
//            UserCartDto userCartDto = buildUserCartDto(user.get(), ticketIdLong);
//            String status = ticketService.getTicketStatus(ticketIdLong);
//            if (status != null) {
//                handleAction(action, status, userCartDto, response);
//            } else {
//                sendErrorResponse(response, "Ticket not found");
//            }
//        }
//    }
//
//    private void handleClearAction(UserDto user, HttpServletResponse response) throws IOException, DaoCrudException {
//        userCartService.clearUserCart(user.getId());
//        sendSuccessResponse(response);
//    }
//
//    private UserCartDto buildUserCartDto(UserDto user, Long ticketIdLong) {
//        return UserCartDto.builder()
//                .userId(user.getId())
//                .ticketId(ticketIdLong)
//                .build();
//    }
//
//    private void handleAction(String action, String status, UserCartDto userCartDto, HttpServletResponse response) throws IOException {
//        switch (action) {
//            case "add" -> {
//                if ("AVAILABLE".equals(status)) {
//                    userCartService.addItemToCart(userCartDto);
//                    sendSuccessResponse(response);
//                } else {
//                    sendErrorResponse(response, "Concurrency error");
//                }
//            }
//            case "remove" -> {
//                if ("RESERVED".equals(status)) {
//                    userCartService.removeItemFromCart(userCartDto);
//                    sendSuccessResponse(response);
//                } else {
//                    sendErrorResponse(response, "Invalid ticket status");
//                }
//            }
//            default -> sendErrorResponse(response, "Invalid action");
//        }
//    }
//
//    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        response.getWriter().write("{\"success\": true}");
//    }
//
//    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
//        response.setContentType("application/json");
//        response.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
//    }
//}