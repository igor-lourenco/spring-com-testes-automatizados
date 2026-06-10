package com.expert.testes.security;

import com.expert.testes.entities.Role;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.security.clientSettings.PRIVATE_KEY_JWT.KeyPrinter;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Configuration
@Log4j2
public class JwtConfig {

    /*  ====== Fluxo ======
        Gera par de chaves RSA
                ↓
        Monta objeto RSAKey
                ↓
        Coloca em um JWKSet
                ↓
        Entrega isso ao Spring Authorization Server
                ↓
        Quando o token é criado, adiciona claims customizadas
    */


    /* Esse método fornece a chave que será usada para assinar o TOKEN JWT, o Spring Authorization Server usa para:
    * - assinar o token JWT
    * - expor a chave pública para validação do token
    * - permitir que o servidor escolha qual chave usar
    * - automaticamente expõe /oauth2/jwks
    */
    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyHolder keyHolder, KeyPrinter keyPrinter /*O KeyPrinter não é necessário, apenas para teste*/) {
        log.info(">>> FORNECENDO AS CHAVES PARA O SPRIG AUTHORIZATION SERVER ASSINAR O TOKEN JWT");
        JWKSet jwkSet = new JWKSet(keyHolder.getRsaKey());
//        return (selector, context) -> selector.select(jwkSet);

//      TODO: Carregando as chaves do arquivo salvo - apenas para teste, depois remover
        log.warn(">>> CARREGANDO E PRINTANDO AS CHAVES GERADAS (APENAS PARA TESTE, DEPOIS REMOVER)");
        keyPrinter.printKeys();

        log.warn(">>> PEGANDO A PRIVATE KEY DO ARQUIVO: {} (APENAS PARA TESTE, DEPOIS REMOVER)", keyPrinter.getPathPrivateKey().toAbsolutePath());
        PrivateKey privateKey = keyHolder.loadPrivateKey(keyPrinter.getPathPrivateKey());

        log.warn(">>> PEGANDO A PUBLIC KEY DO ARQUIVO: {} (APENAS PARA TESTE, DEPOIS REMOVER)", keyPrinter.getPathPublicKey().toAbsolutePath());
        PublicKey publicKey = keyHolder.loadPublicKey(keyPrinter.getPathPublicKey());

        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) publicKey)
            .privateKey((RSAPrivateKey) privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();

        JWKSet jwkSet2 = new JWKSet(rsaKey);

        return (selector, context) -> selector.select(jwkSet2);
    }


    @Bean // Esse bean serve para customizar o conteúdo do TOKEN JWT no momento em que ele está sendo gerado, serve para adicionar ou alterar claims no TOKEN JWT antes dele ser emitido.
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        log.info(">>> CARREGANDO BEAN PARA CUSTOMIZACAO DAS CLAIMS NA GERACAO DO TOKEN JWT");
        return context -> {
            Authentication authentication = context.getPrincipal();

            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                if (authentication instanceof OAuth2ClientAuthenticationToken) { // para verificar se o authentication é do fluxo client_credentials

                    var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                    Objects.requireNonNull(requestAttributes, () -> {
                        log.warn("requestAttributes não pode ser null...");
                        throw new UsernameNotFoundException("");
                    });

                    var request = requestAttributes.getRequest();
                    var email = request.getHeader("username");
                    var password = request.getHeader("password");

                    Objects.requireNonNull(email, () -> {
                        log.warn("Header 'username' não foi passado no header da requisição...");
                        throw new UsernameNotFoundException("");
                    });

                    Objects.requireNonNull(password, () -> {
                        log.warn("Header 'password' não foi passado no header da requisição...");
                        throw new UsernameNotFoundException("");
                    });

                    log.info(">>> Buscando no banco de dados o username: {}", email);
                    User user = userRepository.findByEmail(email).orElseThrow(() -> {
                        log.warn("User não encontrado para o username: {}", email);
                        throw  new UsernameNotFoundException("");
                    });

                    if(!passwordEncoder.matches(password, user.getPassword())){
                        log.warn("Senha inválida para o username: {}", email);
                        throw  new UsernameNotFoundException("");
                    }


                    Set<String> authorities = new HashSet<>();
                    for (Role role : user.getRoles()) {
                        authorities.add(role.getAuthority());
                    }

                    log.info(">>> Adicionando claim customizada user_id: {}", user.getId().toString());
                    context.getClaims().claim("user_id", user.getId().toString()); // adiciona o id do usuario no token

                    log.info(">>> Adicionando claim customizada user_email: {}", user.getEmail());
                    context.getClaims().claim("user_email", user.getEmail()); // adiciona o email do usuario no token

                    log.info(">>> Adicionando claim customizada authorities: {}", authorities);
                    context.getClaims().claim("authorities", authorities); // adiciona lista de authorities do usuario no token
                }
            }

        };
    }
}
