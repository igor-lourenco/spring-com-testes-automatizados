package com.expert.testes.utils;

import com.expert.testes.entities.Category;

import java.time.LocalDateTime;
import java.util.Set;

public class CategoryFactory {

    public static Category createCategory(){
        return Category.builder()
            .id(1L)
            .name("Category Mock")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .products(Set.of(ProductFactory.createWithoutCategory()))
            .build();

    }

    public static Category createCategory(long categoryId){
        return Category.builder()
            .id(categoryId)
            .name("Category Mock")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .products(Set.of(ProductFactory.createWithoutCategory()))
            .build();

    }
}
