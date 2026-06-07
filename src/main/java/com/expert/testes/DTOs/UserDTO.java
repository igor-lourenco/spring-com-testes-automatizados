package com.expert.testes.DTOs;

import com.expert.testes.entities.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos com valores nulos durante a serialização para JSON
public record UserDTO(
    Long id,
    String firstName,
    String lastName,
    String email,

    @JsonProperty("roles")
    List<RoleDTO> rolesDTO) {

    public UserDTO(User user) {
        this(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRoles() == null
                ? new ArrayList<>()
                : user.getRoles().stream().map(RoleDTO::new).toList()
        );
    }
}
