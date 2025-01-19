package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.PurchasedTicketsDao;
import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.exception.ValidationException;

import java.util.List;

/**
 * Service class for managing purchased tickets.
 */
public class PurchasedTicketsService {
    private static final PurchasedTicketsService INSTANCE = new PurchasedTicketsService();
    private final PurchasedTicketsDao purchasedTicketsDao = PurchasedTicketsDao.getInstance();
    private PurchasedTicketsService(){}
    public static PurchasedTicketsService getInstance(){
        return INSTANCE;
    }

    /**
     * Creates new purchased tickets record
     *
     * @param ticketIds - provided IDs of tickets to update as 'SOLD' after purchase committed
     * @param userId - the ID of the user who purchases the tickets
     */
    public void savePurchasedTickets(List<Long> ticketIds, Long userId) {
        purchasedTicketsDao.save(ticketIds, userId);
    }

    /**
     * Finds all tickets purchased by a specific user
     *
     * @param userId - provided ID of the user
     * @return list of all tickets purchased by a specific user
     */
    public List<PurchasedTicketDto> findAllByUserId(Long userId) {
        return purchasedTicketsDao.findAllByUserId(userId);
    }
}
