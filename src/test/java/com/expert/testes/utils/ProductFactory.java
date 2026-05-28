package com.expert.testes.utils;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class ProductFactory {

    public static Product createWitCategory(Long productId, Long categoryId){
        Set<Category> categorySet = new HashSet<>();
        categorySet.add(CategoryFactory.createCategory(categoryId));

        return Product.builder()
            .id(productId)
            .name("Phone")
            .description("Good Phone")
            .price(new BigDecimal(800.0))
            .imgUrl("https://img.com/phone.png")
            .categories(categorySet)
            .build();
    }

    public static Product createWithoutCategory(){
        return Product.builder()
            .name("Phone")
            .description("Good Phone")
            .price(new BigDecimal(800.0))
            .imgUrl("https://img.com/phone.png")
            .categories(new HashSet<>())
            .build();
    }

    public static Product createWithoutCategory(Long productId){
        return Product.builder()
            .id(productId)
            .name("Phone")
            .description("Good Phone")
            .price(new BigDecimal(800.0))
            .imgUrl("https://img.com/phone.png")
            .categories(new HashSet<>())
            .build();
    }

    public static ProductDTO createDTOWithoutCategory(){
        Product product = createWithoutCategory();
        return new ProductDTO(product, product.getCategories());
    }

    public static ProductDTO createDTOWithoutCategory(Long productId){
        Product product = createWithoutCategory(productId);
        return new ProductDTO(product, product.getCategories());
    }

    public static ProductDTO createDTOWithCategoryDTO(Long productId, Long categoryId){
        Product product = createWitCategory(productId, categoryId);
        return new ProductDTO(product, product.getCategories());
    }
}
