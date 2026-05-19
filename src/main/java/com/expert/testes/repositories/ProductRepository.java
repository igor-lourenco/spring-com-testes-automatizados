package com.expert.testes.repositories;

import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
