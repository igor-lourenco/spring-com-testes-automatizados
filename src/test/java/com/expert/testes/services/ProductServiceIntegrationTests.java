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
//      Os valores agora tem que ser reais porque vai ser testado o banco de dados
        existingId = 1L;
        nonExistingId = 999L;
        nonExistingCategoryId = 999L;
        existingCategoryId = 1L;
    }



//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]



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
