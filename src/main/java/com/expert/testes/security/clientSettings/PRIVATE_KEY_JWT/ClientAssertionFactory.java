package com.expert.testes.security.clientSettings.PRIVATE_KEY_JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class ClientAssertionFactory {


    public static void main(String[] args) throws Exception {

        Path path = Paths.get("keys/private-key.pem");
        String pem = Files.readString(path);

        PrivateKey privateKey = loadPrivateKey(pem);

        String clientId =  "myclientidprivatekey";
            String tokenEndpoint = "http://localhost:9200/oauth2/token";
        String jwt = createJwt( clientId, privateKey, tokenEndpoint);

        System.out.println("CLIENT ASSERTION:\n" + jwt);
    }


    public static PrivateKey loadPrivateKey(String pem) throws Exception {

        String cleaned = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(cleaned);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }


    public static String createJwt(String clientId, PrivateKey privateKey, String tokenEndpoint) {

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(300); // 300seg = 5 min

        return Jwts.builder()
            .issuer(clientId)                        // iss
            .subject(clientId)                       // sub = client_id
            .audience().add(tokenEndpoint).and()        // aud
            .issuedAt(Date.from(now))      // iat
            .expiration(Date.from(exp))    // exp
            .id(UUID.randomUUID().toString())        // jti
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
    }

}
