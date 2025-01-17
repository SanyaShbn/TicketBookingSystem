package com.example.ticketbookingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Корзина билетов пользователя
@Entity
@Table(name = "user_cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCart {

    @EmbeddedId
    private UserCartId id;
}
