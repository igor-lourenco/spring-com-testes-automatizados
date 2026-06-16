package com.expert.testes.services;

import com.expert.testes.DTOs.CategoryDTO;
import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;
import com.expert.testes.projections.ProductProjection;
import com.expert.testes.repositories.CategoryRepository;
import com.expert.testes.repositories.ProductRepository;
import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.services.exceptions.NumeroFormatException;
import com.expert.testes.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPagedProductProjection(String name, String categoryId, Pageable pageable) {
        try {
            List<Long> categoryIds = "0".equals(categoryId) ? new ArrayList<>()
                : Arrays.asList(categoryId.split(",")).stream().map(Long::parseLong).toList();

            Page<ProductProjection> page = repository.searchProducts(categoryIds, name, pageable);

            List<Long> productIds = page.map(ProductProjection::getId).toList();
            List<Product> products = repository.searchProductsWithCategories(productIds);

            products = Utils.replace(page.getContent(), products);

            List<ProductDTO> productDTOs = products.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();

            return new PageImpl<>(
                productDTOs,
                page.getPageable(),
                page.getTotalElements());

        } catch (NumberFormatException e) {
            throw new NumeroFormatException("Parâmetro categoryId só pode conter números");
        }
    }


    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return repository.findAll().stream()
            .map(ProductDTO::new)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<ProductDTO> page = repository.findAll(pageable).map(ProductDTO::new);

//        return new PageImpl<>(
//            new ArrayList<>(page.getContent()),
//            page.getPageable(),
//            page.getTotalElements());

        return page;
    }


    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = findProductById(id);
        return new ProductDTO(product, product.getCategories());
    }


    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {
        try {

            Product product = new Product();
            convertToProduct(productDTO, product);
            product = repository.save(product);
            return new ProductDTO(product, product.getCategories());

        } catch (EntityNotFoundException e) {
            if (e.getCause() instanceof ObjectNotFoundException obj) {
                throw new EntidadeNotFoundException("Category não encontrado: " + obj.getIdentifier() + ", para associar com Product");
            }
            throw e;
        }
    }


    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        try {

            Product product = findProductById(id);
            convertToProduct(productDTO, product);
            return new ProductDTO(product, product.getCategories());

        } catch (EntityNotFoundException e) {
            if (e.getCause() instanceof ObjectNotFoundException obj) {
                throw new EntidadeNotFoundException("Category não encontrado: " + obj.getIdentifier() + ", para associar com Product");
            }
            throw e;
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS) //Participa de uma transação existente, mas NÃO cria uma nova
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNotFoundException("Product não encontrado: " + id);
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }


    private Product findProductById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new EntidadeNotFoundException("Product não encontrado: " + id));
    }


    private void convertToProduct(ProductDTO productDTO, Product product) {
        product.setName(productDTO.name());
        product.setDescription(productDTO.description());
        product.setPrice(productDTO.price());
        product.setImgUrl(productDTO.imgUrl());
        product.getCategories().clear();

        if(productDTO.categoryDTOS() == null) return;

        for (CategoryDTO categoryDTO : productDTO.categoryDTOS()) {
            //getReferenceById ⇾ ideal para salvar ou atualizar relacionamentos sem precisar carregar dados desnecessários do banco.
            Category category = categoryRepository.getReferenceById(categoryDTO.id());
            product.getCategories().add(category);
        }
    }

}
