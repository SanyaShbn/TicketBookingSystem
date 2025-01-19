package com.example.ticketbookingsystem.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an embeddable entity for identifying user carts
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCartId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
}

