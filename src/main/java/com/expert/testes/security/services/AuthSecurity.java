package com.expert.testes.security.services;

import com.expert.testes.entities.User;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/** Esse componente Spring é utilizado para gerenciar a segurança da aplicação. Contém métodos para verificar se um
 * usuário tem permissão para acessar certos recursos, verificando suas permissões e definindo regras de acesso */
@Log4j2
@Component
@RequiredArgsConstructor
public class AuthSecurity {

    private final UserRepository repository;

    /** Esse método retorna o id do usuário autenticado na requisição atual*/
    public User getUserId() {
        Authentication authentication = getAuthentication();

        if(null == authentication || !(authentication.getPrincipal() instanceof Jwt)) {
            log.error("Não foi encontrado autenticação: {}", authentication.getPrincipal());
            throw new UsernameNotFoundException("O user não foi autenticado.");
        }

//      Pega o JWT da autenticação para extrai informações dele.
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String userId = jwt.getClaim("user_id");
        String userEmail = jwt.getClaim("user_email");

        log.info("User autenticado: {} - {}", userId, userEmail);

        if(null == userId)  throw new EntidadeNotFoundException("User não encontrado para o id: " + userId);

        return repository.findByEmail(userEmail).orElseThrow(() -> {
            log.warn("User não encontrado para o email: {}", userEmail);
            throw new EntidadeNotFoundException("User não encontrado para o email: " + userEmail);
        });
    }

    /** Obtém a autenticação atual do contexto de segurança, ou um token de solicitação de autenticação.*/
    private Authentication getAuthentication() {
        return SecurityContextHolder // Classe que mantém o contexto de segurança do aplicativo.
            .getContext() // obtém o contexto de segurança atual.
            .getAuthentication(); // obtém a autenticação atual do contexto de segurança.
    }
}
