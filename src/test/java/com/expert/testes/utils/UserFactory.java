package com.expert.testes.utils;

import com.expert.testes.entities.Role;
import com.expert.testes.entities.User;

import java.util.Set;

public class UserFactory {

    public static User createUserExisting(){
        return User.builder()
            .id(1L)
            .firstName("Alex")
            .lastName("Green")
            .email("alex@gmail.com")
            .roles(Set.of(new Role(2L, "ROLE_OPERATOR")))
            .build();
    }
}
