package com.expert.testes.DTOs;

import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos com valores nulos durante a serialização para JSON
public record ProductDTO(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String imgUrl,

    @JsonProperty("categories")
    List<CategoryDTO> categoryDTOS) {

    public ProductDTO(Product product) {
        this(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getImgUrl(),
            null
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
