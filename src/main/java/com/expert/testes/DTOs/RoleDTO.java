package com.expert.testes.DTOs;

import com.expert.testes.entities.Role;

public record RoleDTO(
    Long id,
    String authority) {

    public RoleDTO(Role role) {
        this(role.getId(), role.getAuthority());
    }
}
