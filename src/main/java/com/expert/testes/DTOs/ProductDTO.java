package com.expert.testes.DTOs;

import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ProductDTO(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String imgUrl,
    List<CategoryDTO> categoryDTOS) {

    public ProductDTO(Product product) {
        this(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getImgUrl(),
            new ArrayList<>()
        );
    }

    public ProductDTO(Product product, Set<Category> categories) {
        this(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getImgUrl(),
            categories == null ?
                new ArrayList<>() :
                categories.stream().map(CategoryDTO::new).toList()
        );
    }
}
