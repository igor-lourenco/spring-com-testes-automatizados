package com.expert.testes.controllers;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/products")
public class ProductController {

    private final ProductService service;


    @GetMapping(value = "/page/projections")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDTO> findAllPagedProductProjection(
        @RequestParam(value = "name", defaultValue = "") String name,
        @RequestParam(value = "categoryId", defaultValue = "0") String categoryId,
        @PageableDefault(page = 0, size = 12, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("REQUEST - GET [findAllPagedProductProjection]");

        Page<ProductDTO> productDTOs = service.findAllPagedProductProjection(name, categoryId, pageable);

        log.info("RESPONSE - GET [findAllPagedProductProjection]");
        return productDTOs;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> findAll() {
        log.info("REQUEST - GET [findAll]");

        List<ProductDTO> productDTOs = service.findAll();

        log.info("RESPONSE - GET [findAll]");
        return productDTOs;
    }


    @GetMapping(value = "/page")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDTO> findAllPaged(
            @PageableDefault(page = 0, size = 12, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("REQUEST - GET [findAllPaged]");

        Page<ProductDTO> productDTOs = service.findAllPaged(pageable);

        log.info("RESPONSE - GET [findAllPaged]");
        return productDTOs;
    }


    @GetMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDTO findById(@PathVariable Long id) {
        log.info("REQUEST - GET [findById]");

        ProductDTO productDTO = service.findById(id);

        log.info("RESPONSE - GET [findById]");
        return productDTO;
    }


    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        log.info("REQUEST - POST [insert]");

        ProductDTO productDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(productDTO.id())
            .toUri();

        log.info("RESPONSE - POST [insert]");
        return ResponseEntity.created(uri).body(productDTO);
    }


    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDTO update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        log.info("REQUEST - PUT [update]");

        ProductDTO productDTO = service.update(id, dto);

        log.info("RESPONSE - PUT [update]");
        return productDTO;
    }


    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("REQUEST - DELETE [delete]");

        service.delete(id);

        log.info("RESPONSE - DELETE [delete]");
    }


}
