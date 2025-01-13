package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.PurchasedTicketsDao;
import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.exception.ValidationException;

import java.util.List;

public class PurchasedTicketsService {
    private static final PurchasedTicketsService INSTANCE = new PurchasedTicketsService();
    private final PurchasedTicketsDao purchasedTicketsDao = PurchasedTicketsDao.getInstance();
    private PurchasedTicketsService(){}
    public static PurchasedTicketsService getInstance(){
        return INSTANCE;
    }

    public void savePurchasedTickets(List<Long> ticketIds, Long userId) {
        purchasedTicketsDao.save(ticketIds, userId);
    }

    public List<PurchasedTicketDto> findAllByUserId(Long userId) {
        return purchasedTicketsDao.findAllByUserId(userId);
    }
}
