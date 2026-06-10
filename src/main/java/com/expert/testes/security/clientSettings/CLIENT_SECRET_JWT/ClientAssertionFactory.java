package com.expert.testes.security.clientSettings.CLIENT_SECRET_JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class ClientAssertionFactory {


    public static void main(String[] args) {
        String jwt = ClientAssertionFactory.createClientAssertion(
            "myclientidsecretjwt",
            "0123456789abcdef0123456789abcdef",
            "http://localhost:9200/oauth2/token"
        );

        System.out.println("CLIENT ASSERTION:\n" + jwt);
    }

    public static String createClientAssertion(String clientId, String clientSecret, String tokenEndpoint) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(300); // 300seg = 5 min

        SecretKey key = Keys.hmacShaKeyFor(clientSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
            .issuer(clientId)                       // iss
            .subject(clientId)                      // sub = client_id
            .audience().add(tokenEndpoint).and()       // aud
            .issuedAt(Date.from(now))     // iat
            .expiration(Date.from(exp))   // exp
            .id(UUID.randomUUID().toString())       // jti
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}