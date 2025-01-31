//package com.example.ticketbookingsystem.controller;
//
//import com.example.ticketbookingsystem.dto.UserCartDto;
//import com.example.ticketbookingsystem.entity.User;
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.service.TicketService;
//import com.example.ticketbookingsystem.service.UserCartService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//@Controller
//@RequestMapping("/user_cart")
//@RequiredArgsConstructor
//@Slf4j
//public class UserCartController {
//
//    private final UserCartService userCartService;
//
//    private final TicketService ticketService;
//
//    @GetMapping
//    public String getViewAvailableTickets() {
//        return "tickets-purchases-jsp/view-available-tickets";
//    }
//
//    @PostMapping
//    public String manageUserCart(HttpServletRequest request, Model model) {
//        User user = (User) request.getSession().getAttribute("user");
//        String ticketId = request.getParameter("ticketId");
//        String action = request.getParameter("action");
//
//        if ("clear".equals(action) || ticketId == null || ticketId.isEmpty()) {
//            handleClearAction(user, model);
//        } else {
//            Long ticketIdLong = Long.parseLong(ticketId);
//            UserCartDto userCartDto = buildUserCartDto(user, ticketIdLong);
//            String status = ticketService.getTicketStatus(ticketIdLong);
//            if (status != null) {
//                handleAction(action, status, userCartDto, model);
//            } else {
//                model.addAttribute("error", "Ticket not found");
//            }
//        }
//        return "redirect:/user_cart";
//    }
//
//    private UserCartDto buildUserCartDto(User user, Long ticketIdLong) {
//        return UserCartDto.builder()
//                .userId(user.getId())
//                .ticketId(ticketIdLong)
//                .build();
//    }
//
//    private void handleClearAction(User user, Model model) throws DaoCrudException {
//        userCartService.clearUserCart(user.getId());
//        model.addAttribute("success", true);
//    }
//
//    private void handleAction(String action, String status, UserCartDto userCartDto, Model model) {
//        switch (action) {
//            case "add" -> {
//                if ("AVAILABLE".equals(status)) {
//                    userCartService.addItemToCart(userCartDto);
//                    model.addAttribute("success", true);
//                } else {
//                    model.addAttribute("error", "Concurrency error");
//                }
//            }
//            case "remove" -> {
//                if ("RESERVED".equals(status)) {
//                    userCartService.removeItemFromCart(userCartDto);
//                    model.addAttribute("success", true);
//                } else {
//                    model.addAttribute("error", "Invalid ticket status");
//                }
//            }
//            default -> model.addAttribute("error", "Invalid action");
//        }
//    }
//}

package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.service.UserCartService;
import com.example.ticketbookingsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/user_cart")
@RequiredArgsConstructor
public class UserCartController {

    private final UserService userService;

    private final UserCartService userCartService;

    private final TicketService ticketService;

    @GetMapping
    public String getUserCartPage() {
        return "tickets-purchases-jsp/view-available-tickets";
    }

    @PostMapping
    @ResponseBody
    public void handleUserCartRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, DaoCrudException {
        Optional<UserDto> user = getAuthenticatedUser();
        if (user.isEmpty()) {
            sendErrorResponse(response, "User not authenticated");
            return;
        }
        String ticketId = request.getParameter("ticketId");
        String action = request.getParameter("action");

        if ("clear".equals(action) || ticketId == null || ticketId.isEmpty()) {
            handleClearAction(user.get(), response);
        } else {
            Long ticketIdLong = Long.parseLong(ticketId);
            UserCartDto userCartDto = buildUserCartDto(user.get(), ticketIdLong);
            String status = ticketService.getTicketStatus(ticketIdLong);
            if (status != null) {
                handleAction(action, status, userCartDto, response);
            } else {
                sendErrorResponse(response, "Ticket not found");
            }
        }
    }

    private Optional<UserDto> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.findByEmail(userDetails.getUsername());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    private void handleClearAction(UserDto user, HttpServletResponse response) throws IOException, DaoCrudException {
        userCartService.clearUserCart(user.getId());
        sendSuccessResponse(response);
    }

    private UserCartDto buildUserCartDto(UserDto user, Long ticketIdLong) {
        return UserCartDto.builder()
                .userId(user.getId())
                .ticketId(ticketIdLong)
                .build();
    }

    private void handleAction(String action, String status, UserCartDto userCartDto, HttpServletResponse response) throws IOException {
        switch (action) {
            case "add" -> {
                if ("AVAILABLE".equals(status)) {
                    userCartService.addItemToCart(userCartDto);
                    sendSuccessResponse(response);
                } else {
                    sendErrorResponse(response, "Concurrency error");
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