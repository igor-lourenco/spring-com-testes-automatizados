package com.expert.testes.utils;

import com.expert.testes.DTOs.NewPasswordDTO;

public class NewPasswordDTOFactory {


    public static NewPasswordDTO createNewPasswordDTOWithTokenValid(){

        return new NewPasswordDTO("token-valido", "123456");
    }

}
