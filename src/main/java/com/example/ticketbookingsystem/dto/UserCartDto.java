package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.entity.Ticket;
import com.example.ticketbookingsystem.entity.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCartDto {
    User user;
    Ticket ticket;
}
