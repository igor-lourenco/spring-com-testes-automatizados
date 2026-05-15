package com.expert.testes.controllers.exceptions;

import com.expert.testes.services.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Log4j2
@ControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request){
        log.error("ERROR [handleEntityNotFoundException] EXCEPTION :: {}, MENSAGEM :: {}", e.getClass().getSimpleName(), e.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        StandardError error = StandardError.createStandardError(status, path, e);

        return ResponseEntity.status(status).body(error);
    }

}
