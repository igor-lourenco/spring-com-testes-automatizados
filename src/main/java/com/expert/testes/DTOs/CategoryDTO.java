package com.expert.testes.DTOs;

import com.expert.testes.entities.Category;

public record CategoryDTO(
    Long id,
    String name) {

    public CategoryDTO(Category category) {
        this(category.getId(), category.getName());
    }
}
