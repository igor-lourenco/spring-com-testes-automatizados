package com.expert.testes.controllers.handlers;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder //  permite que o builder da classe filha tenha acesso a todos os atributos da cadeia de herança
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false) //  compara e gera o código hash considerando apenas as variáveis que foram declaradas na classe atual (subclasse).
public class ValidationError extends StandardError {

    @Setter(AccessLevel.NONE)
    @Builder.Default // Para o @Builder respeitar o valor default e evitar o NullPointerException
    private List<FieldMessage> errors = new ArrayList<>();


    public static ValidationError createStandardError(HttpStatus status, String path, String error, String message){
        return ValidationError.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(error)
            .message(message)
            .path(path)
            .build();
    }

    public void addError (String fieldName, String message){
        this.errors.add(new FieldMessage(fieldName, message));
    }
}
