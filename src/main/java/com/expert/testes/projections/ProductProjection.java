package com.expert.testes.projections;

public interface ProductProjection extends IdProjection<Long> {

    /* O nome do campo retornado da consulta SQL precisa bater com o nome dos getters */

//    Long getId();

    String getName();
}
