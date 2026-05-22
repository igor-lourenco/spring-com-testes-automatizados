package com.expert.testes.utils;

import com.expert.testes.entities.Product;

import java.math.BigDecimal;
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
}
