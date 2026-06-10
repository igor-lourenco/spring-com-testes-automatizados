package com.expert.testes.services.validation;


import com.expert.testes.DTOs.UserWithPasswordDTO;
import com.expert.testes.controllers.handlers.FieldMessage;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserWithPasswordDTO> {

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserWithPasswordDTO dto, ConstraintValidatorContext context) {
        log.info("Validando na criação do user se o email: {} já existe", dto.email());
        List<FieldMessage> list = new ArrayList<>();

//      TODO: coloque a baixo os testes de validação, acrescentando objetos FieldMessage na lista

        User user = repository.findByEmail(dto.email()).orElseThrow(() -> new EntidadeNotFoundException("User não encontrado: " + dto.email()));

        if (user != null) { // adiciona na lista se já tiver algum user com email passado no payload de request
            list.add(new FieldMessage("email", "Email já existe"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                .addConstraintViolation();
        }
        return list.isEmpty();
    }
}