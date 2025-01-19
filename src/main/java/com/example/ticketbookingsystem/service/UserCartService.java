package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.UserCartDao;
import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.UserCart;
import com.example.ticketbookingsystem.mapper.UserCartMapper;

import java.sql.Connection;
import java.util.List;

/**
 * Service class for managing user carts.
 */
public class UserCartService {
    private static final UserCartService INSTANCE = new UserCartService();
    private final UserCartDao userCartDao = UserCartDao.getInstance();
    private final UserCartMapper userCartMapper = UserCartMapper.getInstance();
    private UserCartService(){}

    public static UserCartService getInstance(){
        return INSTANCE;
    }

    /**
     * Clears the records of a user cart.
     *
     * @param userId the ID of the user who is currently using the cart for ticket purchase
     */
    public void clearUserCart(Long userId) {
        userCartDao.clearCart(userId);
    }

    /**
     * Saves new item to a user cart.
     *
     * @param userCartDto the DTO of the user cart to create
     */
    public void addItemToCart(UserCartDto userCartDto) {
        UserCart userCart = userCartMapper.toEntity(userCartDto);
        userCartDao.save(userCart);
    }

    /**
     * Removes a single record from a user cart.
     *
     * @param userCartDto the DTO of the user cart to remove items from
     */
    public void removeItemFromCart(UserCartDto userCartDto) {
        UserCart userCart = userCartMapper.toEntity(userCartDto);
        userCartDao.delete(userCart);
    }

    /**
     * Retrieves the IDs of the tickets added to a user cart.
     *
     * @param userId the ID of the user who is currently using the cart for ticket purchase
     */
    public List<Long> getTicketIds(Long userId) {
        return userCartDao.getTicketIdsFromUserCart(userId);
    }
}
