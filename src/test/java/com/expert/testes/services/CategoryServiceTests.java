package com.expert.testes.services;

import com.expert.testes.DTOs.CategoryDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.repositories.CategoryRepository;
import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.utils.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//@ExtendWith(SpringExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(MockitoExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
public class CategoryServiceTests {

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private CategoryService service;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;


    private Category category;
    private List<Category> categoryList;
    private PageImpl<Category> page;
    private Pageable pageable;


    @BeforeEach
    void setUp() throws Exception {
//      Os valores não têm nenhum vínculo com o banco de dados, são apenas valores de controle para simulação
        existingId = 1L;
        nonExistingId = 999L;
        dependentId = 2L;

        category = CategoryFactory.createCategory(existingId);
        categoryList = Arrays.asList(category);

        page = new PageImpl<>(List.of(category));
        pageable = PageRequest.of(0, 10);

    }

//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]

    @Test //  <findAll> deve <RetornarListCategoryDTO> [quando <>]
    public void findAllShouldReturnListCategoryDTO(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findAll()).thenReturn(categoryList); // repository.findAll → deve retornar um List não vazia


//      -> Act: execute as ações necessárias
        List<CategoryDTO> result = service.findAll();


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).id());

        Mockito.verify( // garante que o método 'repository.findAll' que está dentro do 'service.findAll' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findAll();
    }


    @Test  //  <findAllPaged> deve <RetornarPage> [quando <>]
    public void findAllPagedShouldReturnPage(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findAll(Mockito.any(Pageable.class))).thenReturn(page); // repository.findAll → deve retornar um Page quando receber qualquer objeto do tipo Pageable


//      -> Act: execute as ações necessárias
        Page<CategoryDTO> result = service.findAllPaged(pageable);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify( // garante que o método 'repository.findAll' que está dentro do 'service.findAllPaged' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findAll(pageable);
    }


    @Test  //  <findById> deve <RetornarCategoryDTO> [quando <IdExistir>]
    public void findByIdShouldReturnCategoryDTOWhenIdExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(existingId))
            .thenReturn(Optional.of(category)); // repository.findById → deve retornar Optional de Category quando id existir


//      -> Act: execute as ações necessárias
        CategoryDTO categoryDTO = service.findById(existingId);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(categoryDTO);
        Assertions.assertEquals(1, categoryDTO.id());
        Assertions.assertEquals("Category Mock", categoryDTO.name());


        Mockito.verify( // garante que o método 'repository.findById' que está dentro do 'service.findById' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(existingId);
    }


    @Test  //  <findById> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void findByIdShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(nonExistingId))
            .thenReturn(Optional.empty()); // repository.findById → deve retornar Optional vazio quando id não existir


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Category não encontrado: " + nonExistingId, ex.getMessage());


        Mockito.verify( // garante que o método 'repository.findById' que está dentro do 'service.findById' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(nonExistingId);
    }


    @Test  //  <insert> deve <PersistirObjeto> [quando <IdEhNull>]
    public void insertShouldPersistObjectWhenIdIsNull(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.save(Mockito.any(Category.class)))
            .thenReturn(category); // repository.save → deve retornar um Category quando receber qualquer objeto do tipo Category


//      -> Act: execute as ações necessárias
        CategoryDTO categoryDTO = service.insert(new CategoryDTO(null, "Category Mock"));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(categoryDTO);
        Assertions.assertEquals(existingId, categoryDTO.id());


        Mockito.verify( // garante que o método 'repository.save' que está dentro do 'service.insert' foi usado exatamente 1 vez
                repository,
                Mockito.times(1))
            .save(Mockito.any(Category.class));
    }


    @Test  //  <update> deve <AtualizarEntidade> [quando <IdExistir>]
    public void updateShouldUpdateEntidadeWhenIdExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(existingId))
            .thenReturn(Optional.of(category)); // repository.findById → deve retornar Optional de Category quando id existir


//      -> Act: execute as ações necessárias
        CategoryDTO categoryDTO = service.update(existingId, new CategoryDTO(null, "Category Mock"));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(categoryDTO);


        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(existingId);
    }


    @Test  //  <update> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void updateShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(nonExistingId))
            .thenReturn(Optional.empty()); // repository.findById → deve retornar Optional vazio quando id não existir


//      -> Act: execute as ações necessárias
        Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.update(nonExistingId, new CategoryDTO(null, "Category Mock"));
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(nonExistingId);

        Mockito // garante que o 'repository' que está dentro do 'service.update' não foi usado além do esperado após a execução completa
            .verifyNoMoreInteractions(repository);
    }


    @Test  //  <delete> deve <LancarDatabaseException> [quando <IdEhDependente>]
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.existsById(dependentId))
            .thenReturn(true); // repository.existsById → retorna true quando o id dependente existir

        Mockito.doThrow(DataIntegrityViolationException.class)
            .when(repository).deleteById(dependentId); // repository.deleteById → lançe DataIntegrityViolationException quando deletar id dependente

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
        Mockito.when(repository.existsById(existingId))
            .thenReturn(true); // repository.existsById → retorna true quando o id existir

        Mockito.doNothing().when(repository)
            .deleteById(existingId); //  repository.deleteById → não faz nada quando o id existir


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
