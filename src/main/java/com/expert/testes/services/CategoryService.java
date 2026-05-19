package com.expert.testes.services;

import com.expert.testes.DTOs.CategoryDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.repositories.CategoryRepository;
import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;


    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return repository.findAll().stream()
            .map(CategoryDTO::new)
            .toList();
    }


    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable) {
        Page<CategoryDTO> page = repository.findAll(pageable).map(CategoryDTO::new);

//        return new PageImpl<>(
//            new ArrayList<>(page.getContent()),
//            page.getPageable(),
//            page.getTotalElements());

        return page;
    }


    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category category = findCategoryById(id);
        return new CategoryDTO(category);
    }


    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.name());

        category = repository.save(category);
        return new CategoryDTO(category);
    }


    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = findCategoryById(id);
        category.setName(dto.name());

        return new CategoryDTO(category);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)){
            throw new EntidadeNotFoundException("Category não encontrado: " + id);
        }

        try {
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private Category findCategoryById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new EntidadeNotFoundException("Category não encontrado: " + id));
    }


}
