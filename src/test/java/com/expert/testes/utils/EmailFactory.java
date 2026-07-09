package com.expert.testes.utils;

import com.expert.testes.DTOs.EmailDTO;

public class EmailFactory {

    public static EmailDTO createEmailDTOExisting(){
        return new EmailDTO("usuario@teste.com");
    }
}

