package com.expert.testes.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;


@Configuration
@EnableWebSecurity // Habilita a configuração de segurança da web no Spring Security
public class AuthorizationServerConfig {

    @Bean // Define uma SecurityFilterChain default
    @Order(Ordered.HIGHEST_PRECEDENCE) // Para que as configurações sejam aplicadas com a maior prioridade
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {

        http
//            .oauth2AuthorizationServer((authServer) -> authServer
//                .oidc(Customizer.withDefaults())
//            )

            .oauth2AuthorizationServer( oAuth2AuthorizationServerConfigurer -> {
                   http.securityMatcher(oAuth2AuthorizationServerConfigurer.getEndpointsMatcher());

                }
            )

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
            )
        ;

        return http.build();
    }

    @Bean // Define as configurações do provedor de identidade, incluindo a URL do emissor (issuer)
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
//            .issuer("http://127.0.0.1:9200")
            .build();
    }


    @Bean // Configura serviço de autorização OAuth2 baseado em JDBC para armazenar e gerenciar autorizações de clientes.
    public OAuth2AuthorizationService oAuth2AuthorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository){
        return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
    }

    // define onde e como os clients (RegisteredClient) são armazenados e buscados
    // ou seja, esse bean espera que exista as tabelas oauth2_registered_client, oauth2_authorization, opcional -> oauth2_authorization_consent
    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder, JdbcOperations jdbcOperations) {

        RegisteredClient clientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_BASIC = fluxoClientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_BASIC(passwordEncoder);
        RegisteredClient clientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_JWT = fluxoClientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_JWT(passwordEncoder);
        RegisteredClient clientCredentialsUsandoTokenJWTCom_PRIVATE_KEY_JWT = fluxoClientCredentialsUsandoTokenJWTCom_PRIVATE_KEY_JWT(passwordEncoder);


        // armazena em memória
//        return new InMemoryRegisteredClientRepository(
//            Arrays.asList(
//                algafoodClientCredentialsTokenJWT,
//            ));

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcOperations);

        registeredClientRepository.save(clientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_BASIC);
        registeredClientRepository.save(clientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_JWT);
        registeredClientRepository.save(clientCredentialsUsandoTokenJWTCom_PRIVATE_KEY_JWT);
        return registeredClientRepository;

    }


    private static RegisteredClient fluxoClientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_BASIC(PasswordEncoder passwordEncoder) {
        return RegisteredClient
            .withId("1")
            .clientId("myclientid")
            .clientSecret(passwordEncoder.encode("myclientsecret"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) // O client envia client_id e client_secret no header HTTP Authorization: Basic ....
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // fluxo client credentials
            .scope("READ")
            .scope("WRITE")

            .tokenSettings(TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // Token JWT
                .accessTokenTimeToLive(Duration.ofMinutes(30))
                .build())

            .clientSettings(ClientSettings.builder()
                .build())

            .build();
    }



//    habilita client_secret_jwt para esse client
//    define client_credentials como grant type
//    o JWT de autenticação no token endpoint deve ser assinado com um JwsAlgorithm (aqui, HS256 via HMAC).
    private static RegisteredClient fluxoClientCredentialsUsandoTokenJWTCom_CLIENT_SECRET_JWT(PasswordEncoder passwordEncoder) {
        return RegisteredClient
            .withId("2")
            .clientId("myclientidsecretjwt")

            // o clientSecret funciona como chave simétrica bruta usada para assinar e validar o JWS
            .clientSecret("0123456789abcdef0123456789abcdef")

            // o cliente monta uma JWT assertion assinada com chave simétrica derivada do client_secret e envia essa assertion no request.
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)

            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // fluxo client credentials
            .scope("READ")
            .scope("WRITE")

            .tokenSettings(TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // Token JWT
                .accessTokenTimeToLive(Duration.ofMinutes(30))
                .build())

//          ClientSettings.Builder tem o método tokenEndpointAuthenticationSigningAlgorithm(...)
//          que serve para definir o algoritmo de assinatura do JWT usado no token endpoint para PRIVATE_KEY_JWT e CLIENT_SECRET_JWT
            .clientSettings(ClientSettings.builder()
                .tokenEndpointAuthenticationSigningAlgorithm(MacAlgorithm.HS256)
                .build())

            .build();

    }



//    habilita private_key_jwt para esse client
//    define client_credentials como grant type
//    o JWT de autenticação no token endpoint deve ser assinado com um JwsAlgorithm (aqui, HS256 via HMAC).
    private static RegisteredClient fluxoClientCredentialsUsandoTokenJWTCom_PRIVATE_KEY_JWT(PasswordEncoder passwordEncoder) {
        return RegisteredClient
            .withId("3")
            .clientId("myclientidprivatekey")
            .clientSecret("{noop}myclientsecretprivatekey")

//           cliente monta uma JWT assertion assinada com chave privada e envia essa assertion no request.
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)

            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // fluxo client credentials
            .scope("READ")
            .scope("WRITE")

            .tokenSettings(TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // Token JWT
                .accessTokenTimeToLive(Duration.ofMinutes(30))
                .build())

//          ClientSettings.Builder tem o método tokenEndpointAuthenticationSigningAlgorithm(...)
//          que serve para definir o algoritmo de assinatura do JWT usado no token endpoint para PRIVATE_KEY_JWT e CLIENT_SECRET_JWT
            .clientSettings(ClientSettings.builder()
                .tokenEndpointAuthenticationSigningAlgorithm(SignatureAlgorithm.RS256)
                .jwkSetUrl("http://localhost:9200/oauth2/jwks") // API da chave pública para validar o token vindo da requisição oauth2/token
                .build())

            .build();

    }
}
