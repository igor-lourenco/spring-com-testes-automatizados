package com.expert.testes.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@EnableWebSecurity // Habilita a configuração de segurança da web no Spring Security.
@EnableMethodSecurity(prePostEnabled = true) // Habilita a segurança de métodos em nível global, permitindo a utilização de anotações como @PreAuthorize e @PostAuthorize em seus métodos.
// Isso significa que você pode definir regras de segurança específicas para cada método, como permissões de acesso baseadas em roles ou condições personalizadas.
public class ResourceServerConfig {

    @Value("${cors.origins}")
    private String corsOrigins;


    @Bean // Define uma SecurityFilterChain personalizada usando o HttpSecurity
    @Order(2) // Menor prioridade que o AuthorizationServer
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity httpSecurity) throws Exception {


//      TOKEN JWT
        httpSecurity
            .authorizeHttpRequests(auth -> auth // Libera tudo
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().permitAll()
            )

            .sessionManagement(smc -> smc
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
//
//          .cors(AbstractHttpConfigurer::disable)
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )

            .csrf(csrf -> csrf // Desativa proteção contra CSRF (Cross-Site Request Forgery) porque o ataque de CSRF geralmente depende de um navegador do usuário e de cookies de autenticação
                .ignoringRequestMatchers("/h2-console/**") // Desabilita CSRF para testes (necessário pro H2 funcionar)
                .disable()
            )

            .headers(headers -> headers // Para renderizar H2 no browser (iframe)
                .frameOptions(frame -> frame.disable())
            )

            .oauth2ResourceServer(conf ->
                conf.jwt(jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );


        return httpSecurity.build();
    }


    // Esse método é responsável por ler as informações customizadas do token JWT
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        log.info(">>> CARREGANDO BEAN PARA LER AS CLAIMS CUSTOMIZADAS DO TOKEN JWT");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

            Collection<GrantedAuthority> grantedAuthorities = authoritiesConverter.convert(jwt); // lista de scope
            log.info(">> Lista de scopes: {}", grantedAuthorities);

            List<String> authorities = jwt.getClaimAsStringList("authorities"); // lista de authorities
            log.info(">> Lista de authorities: {}", authorities);

            if (authorities == null) {
                return grantedAuthorities;
            }

            // junta a lista de authorities com a lista de scopes em uma só
            grantedAuthorities.addAll(authorities
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));

            return grantedAuthorities;
        });

        return converter;
    }

    @Bean // monta a regra de CORS da aplicação definindo quais frontends/origens externas podem chamar a API pelo navegador e com quais métodos/headers.
    public CorsConfigurationSource corsConfigurationSource() {

        String[] origins = corsOrigins.split(",");

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList(origins));
        corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }


//    @Bean //dessa forma  usa descoberta automática por endpoint (issuer-uri / jwk-set-uri) que é o caminho padrão documentado para Resource Server
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder
//            .withJwkSetUri("http://localhost:9200/oauth2/jwks")
//            .build();
//    }

//    @Bean // dessa forma usa decoder explícito com chave pública local
    public JwtDecoder jwtDecoder(KeyHolder keyHolder) {

        Path pathPublicKey = Paths.get("keys/public-key.pem");
        log.warn(">>> PEGANDO A PUBLIC KEY DO ARQUIVO PARA DECODER: {}", pathPublicKey);
        PublicKey publicKey = keyHolder.loadPublicKey(pathPublicKey);

        System.out.println(toPem(publicKey));

        return NimbusJwtDecoder
            .withPublicKey((RSAPublicKey) publicKey)
            .build();
    }

    private String toPem(Key key) {
        String type = key instanceof PrivateKey ? "PRIVATE KEY" : "PUBLIC KEY";

        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());

        return "-----BEGIN " + type + "-----\n"
            + base64.replaceAll("(.{64})", "$1\n")
            + "\n-----END " + type + "-----";
    }


}