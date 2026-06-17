package com.expert.testes.DTOs;

import com.expert.testes.entities.User;
import com.expert.testes.services.validation.UserInsertValid;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid // anotação customizada
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos com valores nulos durante a serialização para JSON
public record UserWithPasswordDTO (
    Long id,

    @NotBlank(message = "Campo 'firstName' obrigatório")
    String firstName,
    @NotBlank(message = "Campo 'lastName' obrigatório")
    String lastName,

    @Email(message = "Campo 'email' inválido")
    String email,

    @NotBlank(message = "Campo 'password' obrigatório")
    @Size(min = 8, message = "Campo 'password' deve ter no mínimo 8 caracteres")
    String password) {

    public UserWithPasswordDTO(User user) {
        this(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPassword()
        );
    }
}
