package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.TicketService;
import com.example.ticketbookingsystem.service.UserCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller class for managing user cart in the Ticket Booking System application.
 */
@RestController
@RequestMapping("/api/v1/user_cart")
@RequiredArgsConstructor
public class UserCartController {

    private final UserCartService userCartService;

    private final TicketService ticketService;

    /**
     * Handles GET requests to retrieve user cart info when purchasing tickets.
     *
     * @param userId  The ID of the authenticated user.
     * @return A ResponseEntity containing the list of user cart items.
     */
    @GetMapping
    public ResponseEntity<List<UserCartDto>> getUserCart(@RequestParam Long userId) {
        List<UserCartDto> userCart = userCartService.findItemsInCart(userId);
        return ResponseEntity.ok(userCart);
    }

    /**
     * Handles POST requests to manage user cart actions such as add, remove, or clear.
     *
     * @param userId  The ID of the authenticated user.
     * @param ticketId The ID of the ticket.
     * @param action  The action to be performed (add, remove, clear).
     * @return A ResponseEntity indicating the success or failure of the operation.
     * @throws DaoCrudException If a CRUD exception occurred.
     */
    @PostMapping
    public ResponseEntity<String> handleUserCartRequest(@RequestParam Long userId,
                                                        @RequestParam(required = false) Long ticketId,
                                                        @RequestParam String action) throws DaoCrudException {
        return switch (action) {
            case "clear" -> {
                userCartService.clearUserCart(userId);
                yield new ResponseEntity<>("Cart cleared", HttpStatus.OK);
            }
            case "add" -> {
                if (ticketId == null) {
                    yield new ResponseEntity<>("Ticket ID is required for add action", HttpStatus.BAD_REQUEST);
                }
                yield handleAddAction(userId, ticketId);
            }
            case "remove" -> {
                if (ticketId == null) {
                    yield new ResponseEntity<>("Ticket ID is required for remove action", HttpStatus.BAD_REQUEST);
                }
                yield handleRemoveAction(userId, ticketId);
            }
            default -> new ResponseEntity<>("Invalid action", HttpStatus.BAD_REQUEST);
        };
    }

    private ResponseEntity<String> handleAddAction(Long userId, Long ticketId) throws DaoCrudException {
        UserCartDto userCartDto = buildUserCartDto(userId, ticketId);
        String status = ticketService.getTicketStatus(ticketId);

        if ("AVAILABLE".equals(status)) {
            userCartService.addItemToCart(userCartDto);
            return new ResponseEntity<>("Item added to cart", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Concurrency error", HttpStatus.CONFLICT);
        }
    }

    private ResponseEntity<String> handleRemoveAction(Long userId, Long ticketId) throws DaoCrudException {
        UserCartDto userCartDto = buildUserCartDto(userId, ticketId);
        String status = ticketService.getTicketStatus(ticketId);

        if ("RESERVED".equals(status)) {
            userCartService.removeItemFromCart(userCartDto);
            return new ResponseEntity<>("Item removed from cart", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid ticket status", HttpStatus.BAD_REQUEST);
        }
    }

    private UserCartDto buildUserCartDto(Long userId, Long ticketIdLong) {
        return UserCartDto.builder()
                .userId(userId)
                .ticketId(ticketIdLong)
                .build();
    }

}