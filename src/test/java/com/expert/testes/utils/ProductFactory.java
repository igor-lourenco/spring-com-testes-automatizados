package com.expert.testes.utils;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ProductFactory {

    public static Product createWithoutCategory(){
        return Product.builder()
            .name("Phone")
            .description("Good Phone")
            .price(new BigDecimal(800.0))
            .imgUrl("https://img.com/phone.png")
            .categories(new HashSet<>())
            .build();
    }

    public static ProductDTO createProductDTOWithCategory(){
        Product product = createWithoutCategory();
        product.getCategories().add(createCategory());
        return new ProductDTO(product, product.getCategories());
    }

    private static Category createCategory(){
        return Category.builder()
            .id(1L)
            .name("Category Mock")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .products(Set.of(ProductFactory.createWithoutCategory()))
            .build();

    }
}
