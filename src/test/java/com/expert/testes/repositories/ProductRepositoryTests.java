package com.expert.testes.repositories;

import com.expert.testes.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

@DataJpaTest // Carrega somente os componentes relacionados ao Spring Data JPA. Cada teste é transacional e dá rollback ao final. (teste de unidade: repository)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository; // considera o seed do banco que tiver no arquivo import.sql


//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]

    @Test  //  <Excluir> deve <excluirObjeto> quando <IdExistir>
    public void deleteShouldDeleteObjectWhenIdExists(){
//      Padrão AAA

//   	Arrange: instancie os objetos necessários
        long existingId = 1L;

//      Act: execute as ações necessárias
        repository.deleteById(existingId);

//      Assert: declare o que deveria acontecer (resultado esperado)
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }
}
