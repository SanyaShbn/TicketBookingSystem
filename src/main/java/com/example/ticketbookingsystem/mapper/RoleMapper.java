package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class RoleMapper {

    public Role toRole(String roleName) {
        if (roleName == null) {
            return null;
        }
        return Role.valueOf(roleName);
    }

    public String toRoleName(Role role) {
        if (role == null) {
            return null;
        }
        return role.name();
    }
}