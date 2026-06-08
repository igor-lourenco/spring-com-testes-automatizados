package com.expert.testes.DTOs;

import com.expert.testes.entities.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos com valores nulos durante a serialização para JSON
public record UserDTO(
    Long id,

    @NotBlank(message = "Campo 'firstName' obrigatório")
    String firstName,
    String lastName,

    @Email(message = "Campo 'email' inválido")
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
