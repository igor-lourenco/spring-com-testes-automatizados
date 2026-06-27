package com.expert.testes.services;

import com.expert.testes.DTOs.EmailDTO;
import com.expert.testes.entities.RecoverPassword;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.RecoverPasswordRepository;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.services.exceptions.EmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final RecoverPasswordRepository recoverPasswordRepository;

    private final EmailService emailService;

    @Value("${email.password-recover.token.minutes}")
    private String expiration;
    @Value("${email.password-recover.uri}")
    private String uriRecoverPassword;

    @Transactional
    public boolean recoverToken(EmailDTO dto) {

        User user = repository.findByEmail(dto.email()).orElseThrow(() -> {
            throw new EmailException("Email não encontrado");
        });

        String token = UUID.randomUUID().toString();

        RecoverPassword entity = RecoverPassword.builder()
            .email(dto.email())
            .token(token)
            .expiration(Instant.now().plus(Long.parseLong(expiration), ChronoUnit.MINUTES))
            .build();

        entity = recoverPasswordRepository.save(entity);

        String body = "Acesse o link para definir uma nova senha\n\n" + uriRecoverPassword + "/" + token
            + ".\n\n Validade de " + expiration + " minutos.";


        emailService.sendEmail(dto.email(), "Recuperação de senha", body);
        return true;
    }
}
