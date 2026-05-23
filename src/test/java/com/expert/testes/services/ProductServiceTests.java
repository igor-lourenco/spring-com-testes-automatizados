package com.expert.testes.services;

import com.expert.testes.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

//@ExtendWith(SpringExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(MockitoExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
public class ProductServiceTests {

    private long existingId;

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private ProductService service;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private ProductRepository repository;


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception{
        existingId = 1L;

//      cenário delete: quando id existir
        Mockito.doNothing().when(repository).deleteById(existingId); //  repository.deleteById → não faz nada quando o id existir
        Mockito.when(repository.existsById(existingId)).thenReturn(true); // repository.existsById → retorna true quando o id existir
    }


//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]

    @Test  //  <delete> deve <FazerNada> [quando <IdExistir>]
    public void deleteShouldDoNothingWhenIdExists(){

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });


//      para auditar comportamento: garante que o método do 'repository.deleteById' que está dentro do 'service.delete' foi chamado exatamente 1 vez
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }
}
