package com.expert.testes.services;

import com.expert.testes.entities.Category;
import com.expert.testes.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;


    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return repository.findAll();
    }

}
