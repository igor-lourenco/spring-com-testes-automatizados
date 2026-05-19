package com.expert.testes.services.exceptions;

public class EntidadeNotFoundException extends RuntimeException{

    public EntidadeNotFoundException(String message) {
        super(message);
    }
}
