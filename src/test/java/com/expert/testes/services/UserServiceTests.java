package com.expert.testes.services;

import com.expert.testes.repositories.UserRepository;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
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
public class UserServiceTests {

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private UserRepository repository;

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private UserService service;


    private long existingId;
    private long nonExistingId;
    private long dependentId;


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception{
//      Os valores não têm nenhum vínculo com o banco de dados, são apenas valores de controle para simulação

        existingId = 1L;
        nonExistingId = 999L;
        dependentId = 2L;



    }


//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]


    @Test  //  <delete> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void deleteShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false); // repository.existsById → retorna false quando o id não existir


//      -> Act: execute as ações necessárias
        Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.existsById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).existsById(nonExistingId);

        Mockito.verify( // garante que o método do 'repository.deleteById' que está dentro do 'service.delete' não tenha usado.
            repository,
            Mockito.never()
        ).deleteById(nonExistingId);
    }


    @Test  //  <delete> deve <FazerNada> [quando <IdExistir>]
    public void deleteShouldDoNothingWhenIdExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.existsById(existingId)).thenReturn(true); // repository.existsById → retorna true quando o id existir
        Mockito.doNothing().when(repository).deleteById(existingId); //  repository.deleteById → não faz nada quando o id existir


//      -> Act: execute as ações necessárias
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.deleteById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).deleteById(existingId);
    }
}
