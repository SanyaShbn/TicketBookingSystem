package com.example.ticketbookingsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Корзина билетов пользователя
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCart {
    private Long id;
    private User user;
    private Ticket ticket;
}
