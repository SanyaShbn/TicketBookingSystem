package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.UserCartDto;
import com.example.ticketbookingsystem.entity.UserCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserCartMapper {

    UserCartMapper INSTANCE = Mappers.getMapper(UserCartMapper.class);

    @Mapping(target = "id", expression = "java(new UserCartId(userCartDto.getUserId(), userCartDto.getTicketId()))")
    UserCart toEntity(UserCartDto userCartDto);

    @Mapping(target = "userId", source = "id.userId")
    @Mapping(target = "ticketId", source = "id.ticketId")
    UserCartDto toDto(UserCart userCart);
}