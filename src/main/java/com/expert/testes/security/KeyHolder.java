package com.expert.testes.security;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Log4j2
@Component
public class KeyHolder { // esse componente cria o par de chaves dinamicamente em toda subida da aplicação e mantém em memória

    private final RSAKey rsaKey;

    public KeyHolder() {
        log.info(">>> CARREGANDO AS CHAVES RSA QUE SERÃO USADAS PARA AUTENTICAÇÃO DO TOKEN JWT");
        this.rsaKey = generateRsa();
    }

    public RSAKey getRsaKey() {
        return rsaKey;
    }

    /* Esse método pega o KeyPair gerado e transforma em um objeto RSAKey, que é o formato esperado pela biblioteca de JWT/JWK */
    private static RSAKey generateRsa() {

        KeyPair keyPair = generateRsaKey();

        log.info(">>> CONVERTENDO PAR DE CHAVES RSA EM OBJETO RSAKEY UTILIZÁVEL PELO AUTHORIZATION SERVER");
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();
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



/*  Esse método serve para carrega a chave privada que foi criado e salvo na subida da aplicação */
    public PrivateKey loadPrivateKey(Path path) {
        try {
            String pem = Files.readString(path);

            String cleaned = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(cleaned);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

            return KeyFactory.getInstance("RSA").generatePrivate(spec);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

/*  Esse método serve para carrega a chave pública que foi criado e salvo na subida da aplicação */
    public PublicKey loadPublicKey(Path path) {
        try {
            String pem = Files.readString(path);

            String cleaned = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(cleaned);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

            return KeyFactory.getInstance("RSA").generatePublic(spec);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}