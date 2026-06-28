package com.expert.testes.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewPasswordDTO(

    @NotBlank(message = "Campo 'token' obrigatório")
    String token,

    @NotBlank(message = "Campo 'password' obrigatório")
    @Size(min = 8, message = "Campo 'password' deve ter no mínimo 8 caracteres")
    String password
) {
}
