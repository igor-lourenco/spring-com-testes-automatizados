package com.expert.testes.services;

import com.expert.testes.DTOs.CategoryDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.repositories.CategoryRepository;
import com.expert.testes.utils.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

//@ExtendWith(SpringExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(MockitoExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
public class CategoryServiceTests {

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private CategoryService service;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private CategoryRepository repository;

    private Category category;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() throws Exception {
//      Os valores não têm nenhum vínculo com o banco de dados, são apenas valores de controle para simulação

        category = CategoryFactory.createCategory(1L);
        categoryList = Arrays.asList(category);
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
}
