package com.expert.testes.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita a configuração de segurança da web no Spring Security
public class SecurityConfig {


    @Bean // Define uma SecurityFilterChain default
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {


//      TODO: Por enquanto todas as APIs estão sendo liberadas
        http
            .authorizeHttpRequests(auth -> auth // Libera tudo
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf // Desativa proteção contra CSRF (Cross-Site Request Forgery) porque o ataque de CSRF geralmente depende de um navegador do usuário e de cookies de autenticação
                .ignoringRequestMatchers("/h2-console/**") // Desabilita CSRF para testes (necessário pro H2 funcionar)
                .disable()
            )
            .headers(headers -> headers // Para renderizar H2 no browser (iframe)
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }
}
