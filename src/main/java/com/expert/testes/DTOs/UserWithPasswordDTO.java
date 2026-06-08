package com.expert.testes.DTOs;

import com.expert.testes.entities.User;
import com.expert.testes.services.validation.UserInsertValid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@UserInsertValid
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos com valores nulos durante a serialização para JSON
public record UserWithPasswordDTO (
    Long id,

    @NotBlank(message = "Campo 'firstName' obrigatório")
    String firstName,
    String lastName,

    @Email(message = "Campo 'email' inválido")
    String email,

    @NotBlank(message = "Campo 'password' obrigatório")
    String password,

    @JsonProperty("roles")
    List<RoleDTO> rolesDTO) {

    public UserWithPasswordDTO(User user) {
        this(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPassword(),
            user.getRoles() == null
                ? new ArrayList<>()
                : user.getRoles().stream().map(RoleDTO::new).toList()
        );
    }
}
