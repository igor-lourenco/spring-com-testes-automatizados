package com.expert.testes.utils;

import com.expert.testes.entities.Role;

public class RoleFactory {

    public static Role createRoleExiting(){
        return new Role(1L, "ROLE_OPERATOR");
    }

    public static Role createRoleDoesNotExiting(){
        return new Role(2L, "ROLE_OPERATOR");
    }
}
