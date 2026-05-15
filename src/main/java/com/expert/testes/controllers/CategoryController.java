package com.expert.testes.controllers;

import com.expert.testes.DTOs.CategoryDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/categories")
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDTO> findAll() {
        log.info("REQUEST - GET [findAll]");

        List<CategoryDTO> categoryDTOs = service.findAll();

        log.info("RESPONSE - GET [findAll]");
        return categoryDTOs;
    }
}
