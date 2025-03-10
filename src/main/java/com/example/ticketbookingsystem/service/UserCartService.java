package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.entity.UserCart;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.mapper.UserCartMapper;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.repository.UserCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user carts.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserCartService {

    private final UserCartRepository userCartRepository;

    private final TicketRepository ticketRepository;

    private final UserCartMapper userCartMapper;

    /**
     * Clears the records of a user cart.
     *
     * @param userId the ID of the user who is currently using the cart for ticket purchase
     */
    @Transactional
    public void clearUserCart(Long userId) {
        List<UserCart> userCarts = userCartRepository.findByUserId(userId);
        List<Ticket> ticketsToUpdate = new ArrayList<>();

        for (UserCart userCart : userCarts) {
            updateUserCartAndCollectTickets(userCart, ticketsToUpdate);
        }

        if (!ticketsToUpdate.isEmpty()) {
            ticketRepository.saveAll(ticketsToUpdate);
            log.info("Ticket statuses successfully updated before clearing User Cart with user ID: {}", userId);
        }
        userCartRepository.deleteByUserId(userId);
        log.info("User Cart is successfully cleared of user tickets with user ID: {}", userId);
    }

    /**
     * Saves new item to a user cart.
     *
     * @param userCartDto the DTO of the user cart to create
     */
    @Transactional
    public void addItemToCart(UserCartDto userCartDto) {
        UserCart userCart = userCartMapper.toEntity(userCartDto);
        Optional<Ticket> ticketOpt = ticketRepository.findById(userCart.getId().getTicketId());

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus(TicketStatus.RESERVED);
            userCartRepository.save(userCart);
            ticketRepository.save(ticket);
            log.info("Ticket item successfully added to User Cart: {}", userCartDto);
        } else {
            log.error("Failed to add ticket item to User Cart: {}", userCartDto);
            throw new DaoCrudException(new Throwable("Ticket not found."));
        }
    }

    /**
     * Removes a single record from a user cart.
     *
     * @param userCartDto the DTO of the user cart to remove items from
     */
    @Transactional
    public void removeItemFromCart(UserCartDto userCartDto) {
        UserCart userCart = userCartMapper.toEntity(userCartDto);
        List<Ticket> ticketsToUpdate = new ArrayList<>();

        if (updateUserCartAndCollectTickets(userCart, ticketsToUpdate)) {
            ticketRepository.saveAll(ticketsToUpdate);
            userCartRepository.delete(userCart);
            log.info("Ticket item successfully removed from User Cart: {}", userCartDto);
        } else {
            log.error("Failed to remove ticket item from User Cart: {}", userCartDto);
            throw new DaoCrudException(new Throwable("Ticket not found."));
        }
    }

    /**
     * Retrieves the IDs of the tickets added to a user cart.
     *
     * @param userId the ID of the user who is currently using the cart for ticket purchase
     */
    @Transactional(readOnly = true)
    public List<Long> getTicketIds(Long userId) {
        List<UserCart> userCarts = userCartRepository.findByUserId(userId);
        return userCarts.stream()
                .map(cart -> cart.getId().getTicketId())
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of tickets in user cart and collects them for batch update.
     *
     * @param userCart        the user cart to update
     * @param ticketsToUpdate the list of tickets to be updated
     * @return true if the ticket is found and updated, false otherwise
     */
    private boolean updateUserCartAndCollectTickets(UserCart userCart, List<Ticket> ticketsToUpdate) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(userCart.getId().getTicketId());

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            if(ticket.getStatus() == TicketStatus.RESERVED) {
                ticket.setStatus(TicketStatus.AVAILABLE);
            }
            ticketsToUpdate.add(ticket);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the list of items in user cart by a specific user ID.
     *
     * @param userId the ID of the user who is currently using the cart for ticket purchase
     * @return list of user cart items
     */
    public List<UserCartDto> findItemsInCart(Long userId) {
        return userCartRepository.findByUserId(userId).stream()
                .map(userCartMapper::toDto)
                .collect(Collectors.toList());
    }
}
