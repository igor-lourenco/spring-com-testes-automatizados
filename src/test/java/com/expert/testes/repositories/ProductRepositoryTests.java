package com.expert.testes.repositories;

import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;
import com.expert.testes.utils.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@DataJpaTest // Carrega somente os componentes relacionados ao Spring Data JPA. Cada teste é transacional e dá rollback ao final. (teste de unidade: repository)
public class ProductRepositoryTests {

    private long existingId;

    @Autowired
    private ProductRepository repository; // considera o seed do banco que tiver no arquivo import.sql
    @Autowired
    private CategoryRepository categoryRepository; // considera o seed do banco que tiver no arquivo import.sql


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp(){
        existingId = 1L;
    }



//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]

    @Test  // <findById> deve <RetornarOptionalNaoVazio> [quando <IdExistir>]
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists(){
//      Padrão AAA

//   	Arrange: instancie os objetos necessários
        Optional<Product> productOptional = Optional.empty();

//      Act: execute as ações necessárias
        productOptional = repository.findById(existingId);

//      Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertTrue(productOptional.isPresent());
    }


    @Test  // <salvar> deve <PersistirObjeto> [quando <IdEhNull>]
    public void saveShouldPersistObjectWhenIdIsNull(){
//      Padrão AAA

//   	Arrange: instancie os objetos necessários
        Category categoryReferenceById = categoryRepository.getReferenceById(8L);
        Product product = ProductFactory.createWithoutCategory();
        product.getCategories().add(categoryReferenceById);

//      Act: execute as ações necessárias
        product = repository.save(product);

//      Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(product.getId(), 51L);
        Assertions.assertEquals(product.getCategories().stream().findFirst().get().getId(), 8L);
    }


    @Test  //  <excluir> deve <excluirObjeto> [quando <IdExistir>]
    public void deleteShouldDeleteObjectWhenIdExists(){
//      Padrão AAA

//   	Arrange: instancie os objetos necessários
//      long existingId = 1L;

//      Act: execute as ações necessárias
        repository.deleteById(existingId);

//      Assert: declare o que deveria acontecer (resultado esperado)
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }
}
