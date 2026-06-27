package com.expert.testes.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO(

    @NotBlank(message = "Campo 'email' obrigatório")
    @Email(message = "Campo 'email' inválido")
    String email) {

}
