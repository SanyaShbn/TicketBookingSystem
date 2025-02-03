package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between User entities and UserDto.
 */
@Mapper(componentModel = "spring", uses = {PasswordMapper.class, RoleMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", qualifiedByName = "toHash")
    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}