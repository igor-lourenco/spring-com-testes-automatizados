package com.expert.testes.services;

import com.expert.testes.DTOs.UserDTO;
import com.expert.testes.entities.Role;
import com.expert.testes.entities.User;
import com.expert.testes.repositories.RoleRepository;
import com.expert.testes.repositories.UserRepository;
import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.utils.RoleFactory;
import com.expert.testes.utils.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

//@ExtendWith(SpringExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(MockitoExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
public class UserServiceTests {

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private UserRepository repository;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private RoleRepository roleRepository;

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private UserService service;


    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private long existingRoleId;
    private long notExistingRoleId;

    private User userExisting;
    private UserDTO userDTODoesNotExisting;
    private UserDTO userDTOExisting;
    private UserDTO userDTOExistingWithRoleDTOIsNull;
    private UserDTO userDTOExistingWithRoleDTODoesNotExisting;
    private Role roleExisting;


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception{
//      Os valores não têm nenhum vínculo com o banco de dados, são apenas valores de controle para simulação

        existingId = 1L;
        nonExistingId = 999L;
        dependentId = 2L;
        existingRoleId = 1L;
        notExistingRoleId = 2L;

        userExisting = UserFactory.createUserExisting();
        userDTOExisting = UserFactory.createdUserDTOExisting();
        userDTOExistingWithRoleDTOIsNull = UserFactory.createdUserDTOExistingWithRoleDTOIsNull();
        userDTODoesNotExisting = UserFactory.createdUserDTODoesNotExisting();
        userDTOExistingWithRoleDTODoesNotExisting = UserFactory.createdUserDTOExistingWithRoleDTODoesNotExisting();
        roleExisting = RoleFactory.createRoleExiting();

    }


//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]



    @Test  //  <update> deve <LancarEntityNotFoundException> [quando <ErroEhGenerico>]
    public void updateShouldThrowEntityNotFoundExceptionWhenErrorIsGeneric(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(existingId))
            .thenReturn(Optional.of(userExisting)); // repository.findById → deve retornar Optional de User quando id existir

        Mockito.doThrow(new EntityNotFoundException("Erro genérico"))
            .when(roleRepository)  // roleRepository.getReferenceById → lança EntityNotFoundException quando roleId não existir (pra cair no throw e)
            .getReferenceById(notExistingRoleId);


//      -> Act: execute as ações necessárias
        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.update(existingId, userDTOExistingWithRoleDTODoesNotExisting);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertTrue(ex.getMessage().contains("Erro genérico"));


        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,Mockito.times(1)
        ).findById(existingId);


        Mockito.verify( // garante que o método do 'roleRepository.getReferenceById' que está dentro do 'service.update' foi usado exatamente 1 vez
            roleRepository,Mockito.times(1)
        ).getReferenceById(notExistingRoleId);


        Mockito // garante que o 'repository' que está dentro do 'service.update' não foi usado além do esperado após a execução completa
            .verifyNoMoreInteractions(repository);
    }


    @Test  //  <update> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void updateShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(nonExistingId))
            .thenReturn(Optional.empty()); // repository.findById → deve retornar Optional vazio quando id não existir


//      -> Act: execute as ações necessárias
        Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.update(nonExistingId, userDTODoesNotExisting);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,Mockito.times(1)
        ).findById(nonExistingId);

        Mockito // garante que o 'repository' que está dentro do 'service.update' não foi usado além do esperado após a execução completa
            .verifyNoMoreInteractions(repository);
    }


    @Test  //  <delete> deve <LancarDatabaseException> [quando <IdEhDependente>]
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.existsById(dependentId)).thenReturn(true); // repository.existsById → retorna true quando o id dependente existir
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId); // repository.deleteById → lançe DataIntegrityViolationException quando deletar id dependente

//      -> Act: execute as ações necessárias
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.existsById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).existsById(dependentId);

        Mockito.verify( // garante que o método do 'repository.deleteById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).deleteById(dependentId);
    }


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
