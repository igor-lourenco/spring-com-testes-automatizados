package com.expert.testes.controllers.handlers;

import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.services.exceptions.NumeroFormatException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@ControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(EntidadeNotFoundException.class)
    public ResponseEntity<StandardError> handleEntidadeNotFoundException(EntidadeNotFoundException e, WebRequest request){
        log.error("ERROR [handleEntidadeNotFoundException] EXCEPTION :: {}, MENSAGEM :: {}", e.getClass().getSimpleName(), e.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        StandardError error = StandardError
            .createStandardError(status, path, "Recurso não encontrado", e);

        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> handleDatabaseException(DatabaseException e, WebRequest request){
        log.error("ERROR [handleDatabaseException] EXCEPTION :: {}, MENSAGEM :: {}", e.getClass().getSimpleName(), e.getMessage());

        HttpStatus status = HttpStatus.CONFLICT;
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        StandardError error = StandardError
            .createStandardError(status, path, "Erro no banco de dados", e);

        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request){
        log.error("ERROR [handleDatabaseException] EXCEPTION :: {}, MENSAGEM :: {}", e.getClass().getSimpleName(), e.getMessage());

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();


        ValidationError error = ValidationError
            .createStandardError(status, path, "Erro de validação", "Erro na validação do(s) campo(s)");

        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            error.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler(NumeroFormatException.class)
    public ResponseEntity<StandardError> handleNumeroFormatException(NumeroFormatException e, WebRequest request){
        log.error("ERROR [handleNumeroFormatException] EXCEPTION :: {}, MENSAGEM :: {}", e.getClass().getSimpleName(), e.getMessage());

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        StandardError error = StandardError
            .createStandardError(status, path, "Erro de formatação", e);

        return ResponseEntity.status(status).body(error);
    }

}
