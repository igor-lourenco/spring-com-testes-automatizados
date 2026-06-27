package com.expert.testes.controllers;

import com.expert.testes.DTOs.EmailDTO;
import com.expert.testes.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping(path = "/recover-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recoverToken(@Valid @RequestBody EmailDTO dto) {
        log.info("REQUEST - POST [recoverToken]");

        log.info("RESPONSE - POST [recoverToken] token enviado:  {}", service.recoverToken(dto));
    }

}
