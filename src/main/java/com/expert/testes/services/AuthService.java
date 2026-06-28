package com.expert.testes.services;

import com.expert.testes.DTOs.EmailDTO;
import com.expert.testes.DTOs.NewPasswordDTO;
import com.expert.testes.entities.RecoverPassword;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.RecoverPasswordRepository;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.services.exceptions.EmailException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final UserRepository repository;
    private final RecoverPasswordRepository recoverPasswordRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Value("${email.password-recover.token.minutes}")
    private String expiration;
    @Value("${email.password-recover.uri}")
    private String uriRecoverPassword;

    @Transactional
    public void recoverToken(EmailDTO dto) {

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
        log.info("Token enviado com sucesso para o email: {}", dto.email());
    }


    @Transactional
    public void saveNewPassword(NewPasswordDTO dto) {

        List<RecoverPassword> recoverPasswords = recoverPasswordRepository.searchValidTokens(dto.token(), Instant.now());

        if(recoverPasswords.isEmpty()){
            throw new EntidadeNotFoundException("Token inválido");
        }

        User user = repository.findByEmail(recoverPasswords.get(0).getEmail()).orElseThrow(() -> {
            throw new EmailException("Email não encontrado");
        });

        user.setPassword(passwordEncoder.encode(dto.password()));

        user = repository.save(user);
        log.info("Senha atualizada com sucesso para o email: {}", user.getEmail());
    }
}
