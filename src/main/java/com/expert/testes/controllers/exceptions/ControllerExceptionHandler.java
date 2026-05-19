package com.expert.testes.controllers.exceptions;

import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        StandardError error = StandardError
            .createStandardError(status, path, "Erro no banco de dados", e);

        return ResponseEntity.status(status).body(error);
    }

}
