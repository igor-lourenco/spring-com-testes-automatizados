package com.expert.testes.services;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.repositories.ProductRepository;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.utils.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest // Carrega o contexto da aplicação (teste de integração)
@Transactional //  para dar rollback automático após cada teste e isolamento total
public class ProductServiceIntegrationTests {

    private long existingId;
    private long nonExistingId;
    private long nonExistingCategoryId;
    private long existingCategoryId;


    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception{
//      Os valores agora têm que ser reais porque vai ser testado o banco de dados
        existingId = 1L;
        nonExistingId = 999L;
        nonExistingCategoryId = 999L;
        existingCategoryId = 1L;
    }



//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]



    @Test  //  <findById> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void findByIdShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExist(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(ex);
        Assertions.assertTrue(ex.getMessage().contains("Product não encontrado"));
        Assertions.assertTrue(ex.getMessage().contains(String.valueOf(nonExistingId)));
    }


    @Test  //  <insert> deve <PersistirObjeto> [quando <IdEhNull>]
    public void insertShouldPersistObjectWhenIdIsNull(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        long countTotalProducts = repository.count();
        ProductDTO productDTOWithNonExistingCategoryDTO = ProductFactory.createDTOWithCategoryDTO(null, existingCategoryId);


//      -> Act: execute as ações necessárias
        ProductDTO productDTO = service.insert(productDTOWithNonExistingCategoryDTO);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(countTotalProducts + 1, repository.count());
        Assertions.assertFalse(productDTO.categoryDTOS().isEmpty());
        Assertions.assertEquals(existingCategoryId, productDTO.categoryDTOS().get(0).id());
    }


    @Test  //  <insert> deve <LancarEntidadeNotFoundException> [quando <CategoryIdNaoExistir>]
    public void insertShouldThrowEntidadeNotFoundExceptionWhenCategoryIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        long countTotalProducts = repository.count();
        ProductDTO productDTOWithNonExistingCategoryDTO = ProductFactory.createDTOWithCategoryDTO(null, nonExistingCategoryId);


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.insert(productDTOWithNonExistingCategoryDTO);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Category não encontrado: " + nonExistingCategoryId + ", para associar com Product", ex.getMessage());
        Assertions.assertEquals(countTotalProducts, repository.count());
    }


    @Test   //  <update> deve <AtualizarEntidade> [quando <IdExistir>]
    public void updateShouldUpdateEntidadeWhenIdExists() {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        ProductDTO productDTOWithExistingCategoryDTO = ProductFactory.createDTOWithCategoryDTO(existingId, existingCategoryId);
        String expectedName = productDTOWithExistingCategoryDTO.name();
        BigDecimal expectedPrice = productDTOWithExistingCategoryDTO.price();


//      -> Act: execute as ações necessárias
        ProductDTO result = service.update(existingId, productDTOWithExistingCategoryDTO);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.id());
        Assertions.assertEquals(expectedName, result.name());
        Assertions.assertEquals(expectedPrice, result.price());
        Assertions.assertEquals(existingCategoryId, result.categoryDTOS().get(0).id());
    }


    @Test  //  <update> deve <LancarEntidadeNotFoundException> [quando <CategoryIdNaoExistir>]
    public void updateShouldThrowEntidadeNotFoundExceptionWhenCategoryIdDoesNotExist(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        ProductDTO productDTOWithNonExistingCategoryDTO = ProductFactory.createDTOWithCategoryDTO(existingId, nonExistingCategoryId);


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.update(existingId, productDTOWithNonExistingCategoryDTO);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Category não encontrado: " + nonExistingCategoryId + ", para associar com Product", ex.getMessage());
    }


    @Test  //  <update> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void updateShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExist(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        ProductDTO productDTOWithCategoryDTOEmpty = ProductFactory.createDTOWithoutCategory(nonExistingId);


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.update(nonExistingId, productDTOWithCategoryDTOEmpty);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Product não encontrado: " + nonExistingId, ex.getMessage());
    }


    @Test  //  <delete> deve <DeletarProduct> [quando <IdExistir>]
    public void deleteShouldDeleteProductWhenIdExist(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        long countTotalProducts = repository.count();


//      -> Act: execute as ações necessárias
        service.delete(existingId);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertFalse(repository.existsById(existingId));
        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }


    @Test  //  <delete> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void deleteShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExist(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        long countTotalProducts = repository.count();


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Product não encontrado: " + nonExistingId, ex.getMessage());
        Assertions.assertEquals(countTotalProducts, repository.count());
    }

}
