package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.UserCartDao;
import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.UserCart;

public class UserCartService {
    private final static UserCartService INSTANCE = new UserCartService();
    private final UserCartDao userCartDao = UserCartDao.getInstance();
    private UserCartService(){}

    public static UserCartService getInstance(){
        return INSTANCE;
    }

    public void clearUserCart(Long userId) {
        userCartDao.clearCart(userId);
    }
    public void addItemToCart(UserCartDto userCartDto) {
        UserCart userCart = buildUserCartFromDto(userCartDto);
        userCartDao.save(userCart);
    }
    public void removeItemFromCart(UserCartDto userCartDto) {
        UserCart userCart = buildUserCartFromDto(userCartDto);
        userCartDao.delete(userCart);
    }
    private UserCart buildUserCartFromDto(UserCartDto userCartDto) {
        return UserCart.builder()
                .user(userCartDto.getUser())
                .ticket(userCartDto.getTicket())
                .build();
    }
}
