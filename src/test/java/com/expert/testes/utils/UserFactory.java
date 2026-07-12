package com.expert.testes.utils;

import com.expert.testes.DTOs.UserDTO;
import com.expert.testes.DTOs.UserWithPasswordDTO;
import com.expert.testes.entities.Role;
import com.expert.testes.entities.User;

import java.util.HashSet;
import java.util.Set;

public class UserFactory {

    public static User createUserExisting(){
        Set<Role> roles =  new HashSet<>();
        roles.add(RoleFactory.createRoleExiting());

        return User.builder()
            .id(1L)
            .firstName("Alex")
            .lastName("Green")
            .email("usuario1@teste.com")
            .password("senha-antiga")
            .roles(roles)
            .build();
    }

    public static UserDTO createdUserDTOExisting(){
        Set<Role> roles =  new HashSet<>();
        roles.add(RoleFactory.createRoleExiting());

        User user = User.builder()
            .id(1L)
            .firstName("Alex")
            .lastName("Green")
            .email("usuario1@teste.com")
            .password("senha-antiga")
            .roles(roles)
            .build();

        return new UserDTO(user);
    }


    public static UserDTO createdUserDTOExistingWithRoleDTOIsNull(){

        UserDTO userDTO = new UserDTO(
            1L,
            "Alex",
            "Green",
            "usuario1@teste.com",
            null
        );

        return userDTO;
    }

    public static UserDTO createdUserDTODoesNotExisting(){
        Set<Role> roles =  new HashSet<>();
        roles.add(RoleFactory.createRoleExiting());

        User user = User.builder()
            .id(2L)
            .firstName("Bob")
            .lastName("Blue")
            .email("usuario2@teste.com")
            .password("senha-antiga")
            .roles(roles)
            .build();

        return new UserDTO(user);
    }

    public static UserDTO createdUserDTOExistingWithRoleDTODoesNotExisting(){
        Set<Role> roles =  new HashSet<>();
        roles.add(RoleFactory.createRoleDoesNotExiting());

        User user = User.builder()
            .id(1L)
            .firstName("Bob")
            .lastName("Blue")
            .email("usuario2@teste.com")
            .password("senha-antiga")
            .roles(roles)
            .build();

        return new UserDTO(user);
    }


    public static UserWithPasswordDTO createdUserWithPasswordDTOExistingWithRoleDTODoesNotExisting(){
//        Set<Role> roles =  new HashSet<>();
//        roles.add(RoleFactory.createRoleDoesNotExiting());

        User user = User.builder()
            .id(1L)
            .firstName("Bob")
            .lastName("Blue")
            .email("usuario2@teste.com")
            .password("senha-antiga")
//            .roles(roles)
            .build();

        return new UserWithPasswordDTO(user);
    }


    public static UserWithPasswordDTO createdUserWithPasswordDTOExisting(){
//        Set<Role> roles =  new HashSet<>();
//        roles.add(RoleFactory.createRoleDoesNotExiting());

        User user = User.builder()
            .id(1L)
            .firstName("Bob")
            .lastName("Blue")
            .email("usuario2@teste.com")
            .password("senha-antiga")
//            .roles(roles)
            .build();

        return new UserWithPasswordDTO(user);
    }
}
