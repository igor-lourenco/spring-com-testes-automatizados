package com.expert.testes.security;

import com.expert.testes.entities.Role;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.UserRepository;
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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
    public JWKSource<SecurityContext> jwkSource() {
        log.info(">>> CARREGANDO AS CHAVES RSA QUE SERÃO USADAS PARA AUTENTICAÇÃO DO TOKEN JWT");
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }


    /* Esse método pega o KeyPair gerado e transforma em um objeto RSAKey, que é o formato esperado pela biblioteca de JWT/JWK */
    private static RSAKey generateRsa() {
        log.info(">>> CONVERTENDO PAR DE CHAVES RSA EM OBJETO RSAKEY UTILIZÁVEL PELO AUTHORIZATION SERVER");
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }

    /* Esse método gera de fato o par de chaves RSA */
    private static KeyPair generateRsaKey() {
        log.info(">>> GERANDO O PAR DE CHAVES RSA QUE SERÁ USADO PARA ASSINAR OS TOKENS JWT");
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
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
