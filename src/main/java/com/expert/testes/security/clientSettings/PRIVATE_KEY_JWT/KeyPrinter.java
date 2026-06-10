package com.expert.testes.security.clientSettings.PRIVATE_KEY_JWT;

import com.expert.testes.security.KeyHolder;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

// Esse componente serve apenas para printar no console as chaves publicas e privadas geradas pelo Spring Authorization Server
// no momento da subida da aplicação
@Component
@Getter
public class KeyPrinter {

    private final KeyHolder keyHolder;

    private Path pathPrivateKey;
    private Path pathPublicKey;

    public KeyPrinter(KeyHolder keyHolder) {
        this.keyHolder = keyHolder;

        this.pathPrivateKey = Paths.get("keys/private-key.pem");
        this.pathPublicKey = Paths.get("keys/public-key.pem");
    }

    public void printKeys() {
        try {
            RSAKey rsaKey = keyHolder.getRsaKey();

            PrivateKey privateKey = rsaKey.toPrivateKey();
            PublicKey publicKey = rsaKey.toPublicKey();

            System.out.println("\n==== PRIVATE KEY ====");
            System.out.println(toPem(privateKey));

            System.out.println("\n==== PUBLIC KEY ====");
            System.out.println(toPem(publicKey));

            System.out.println("\n==== SALVANDO PRIVATE KEY ====");
            this.pathPrivateKey = Paths.get("keys/private-key.pem");

            Files.writeString(pathPrivateKey, toPem(privateKey),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println(">>> Arquivo gravado com sucesso em: " + pathPrivateKey.toAbsolutePath());

            System.out.println("==== SALVANDO PUBLIC KEY ====");
            this.pathPublicKey = Paths.get("keys/public-key.pem");

            Files.writeString(pathPublicKey, toPem(publicKey),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println(">>> Arquivo gravado com sucesso em: " + pathPublicKey.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Erro ao carregar as chaves: " + e.getMessage());
        }
    }

    private String toPem(Key key) {
        String type = key instanceof PrivateKey ? "PRIVATE KEY" : "PUBLIC KEY";

        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());

        return "-----BEGIN " + type + "-----\n"
            + base64.replaceAll("(.{64})", "$1\n")
            + "\n-----END " + type + "-----";
    }
}