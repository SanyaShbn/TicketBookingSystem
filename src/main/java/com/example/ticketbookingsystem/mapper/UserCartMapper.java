package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.UserCart;
import com.example.ticketbookingsystem.entity.UserCartId;

/**
 * Mapper class for converting between {@link UserCart} entity and {@link UserCartDto} DTO.
 */
public class UserCartMapper implements Mapper<UserCart, UserCartDto> {

    private static final UserCartMapper INSTANCE = new UserCartMapper();

    private UserCartMapper(){}
    public static UserCartMapper getInstance(){
        return INSTANCE;
    }

    /**
     * Converts an {@link UserCart} to an {@link UserCartDto} entity.
     *
     * @param userCartDto the DTO to convert
     * @return the converted {@link UserCart} entity
     */
    @Override
    public UserCart toEntity(UserCartDto userCartDto) {
        UserCartId userCartId = new UserCartId(userCartDto.getUserId(), userCartDto.getTicketId());
        return UserCart.builder()
                .id(userCartId)
                .build();
    }

    /**
     * Converts an {@link UserCartDto} entity to an {@link UserCart}.
     *
     * @param userCart the entity to convert
     * @return the converted {@link UserCartDto}
     */
    @Override
    public UserCartDto toDto(UserCart userCart) {
        return UserCartDto.builder()
                .userId(userCart.getId().getUserId())
                .ticketId(userCart.getId().getTicketId())
                .build();
    }
}