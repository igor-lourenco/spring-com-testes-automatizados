package com.expert.testes.controllers;

import com.expert.testes.DTOs.CategoryDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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


    @GetMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO findById(@PathVariable Long id) {
        log.info("REQUEST - GET [findById]");

        CategoryDTO categoryDTO = service.findById(id);

        log.info("RESPONSE - GET [findById]");
        return categoryDTO;
    }


    @PostMapping
    public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dto) {
        log.info("REQUEST - POST [insert]");

        CategoryDTO categoryDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(categoryDTO.id())
            .toUri();

        log.info("RESPONSE - POST [insert]");
        return ResponseEntity.created(uri).body(categoryDTO);
    }


    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)

    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        log.info("REQUEST - PUT [update]");

        CategoryDTO categoryDTO = service.update(id, dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(categoryDTO.id())
            .toUri();

        log.info("RESPONSE - PUT [update]");
        return categoryDTO;
    }


}
