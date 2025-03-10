package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.entity.PurchasedTicket;
import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.entity.TicketStatus;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.repository.PurchasedTicketRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.repository.UserCartRepository;
import com.example.ticketbookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing purchased tickets.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchasedTicketsService {

    private final PurchasedTicketRepository purchasedTicketRepository;

    private final TicketRepository ticketRepository;

    private final UserRepository userRepository;

    private final UserCartRepository userCartRepository;

    /**
     * Creates new purchased tickets record
     *
     * @param ticketIds - provided IDs of tickets to update as 'SOLD' after purchase committed
     * @param userId - the ID of the user who purchases the tickets
     */
    @Transactional
    public void savePurchasedTickets(List<Long> ticketIds, Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("Now such User with provided id: {}", userId);
            throw new DaoResourceNotFoundException("User not found with id " + userId);
        }
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        tickets.forEach(ticket -> ticket.setStatus(TicketStatus.SOLD));
        ticketRepository.saveAll(tickets);

        List<PurchasedTicket> purchasedTickets = tickets.stream()
                .map(ticket -> PurchasedTicket.builder()
                        .userId(userId)
                        .purchaseDate(LocalDateTime.now())
                        .ticket(ticket)
                        .build())
                .collect(Collectors.toList());

        purchasedTicketRepository.saveAll(purchasedTickets);
        userCartRepository.deleteByUserId(userId);
        log.info("Ticket purchase successfully committed by user with user ID: {}", userId);
    }

    /**
     * Finds all tickets purchased by a specific user
     *
     * @param userId - provided ID of the user
     * @return list of all tickets purchased by a specific user
     */
    public List<PurchasedTicketDto> findAllByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("Now such User with provided id: {}", userId);
            throw new DaoResourceNotFoundException("User not found with id " + userId);
        }
        List<PurchasedTicket> purchasedTickets = purchasedTicketRepository.findAllByUserId(userId);
        return purchasedTickets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PurchasedTicketDto convertToDto(PurchasedTicket purchasedTicket) {
        Ticket ticket = purchasedTicket.getTicket();
        return PurchasedTicketDto.builder()
                .ticketId(ticket.getId())
                .eventName(ticket.getSportEvent().getEventName())
                .eventDateTime(ticket.getSportEvent().getEventDateTime())
                .arenaName(ticket.getSeat().getRow().getSector().getArena().getName())
                .arenaCity(ticket.getSeat().getRow().getSector().getArena().getCity())
                .sectorName(ticket.getSeat().getRow().getSector().getSectorName())
                .rowNumber(ticket.getSeat().getRow().getRowNumber())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .price(ticket.getPrice())
                .build();
    }
}
