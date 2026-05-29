package com.expert.testes.controllers;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.services.ProductService;
import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.utils.ProductFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@WebMvcTest(ProductController.class) // Carrega o contexto, porém somente da camada web (teste de unidade: controlador)
public class ProductControllerTests {

    private long existingId;
    private long nonExistingId;
    private long nonExistingCategoryId;
    private long existingCategoryId;
    private long dependentId;


    private ProductDTO productDTOWithCategoryDTOEmpty;       // ProductDTO com lista de CategoryDTO vazia
    private PageImpl<ProductDTO> page;
    private ProductDTO productDTOWithNonExistingCategoryId;  // ProductDTO com CategoryDTO não existente
    private ProductDTO productDTOWithCategoryDTO;            // ProductDTO com CategoryDTO existente


    @Autowired
    private MockMvc mockMvc; // serve para simular requisições HTTP sem a necessidade de subir um servidor web real (como o Tomcat)

    @MockitoBean // Usa quando a classe de teste carrega o contexto da aplicação e precisa mockar algum bean do sistema.
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception {
//      Os valores não nenhum vínculo com o banco de dados, são apenas valores de controle para simulação
        existingId = 1L;
        dependentId = 2L;
        nonExistingId = 999L;
        nonExistingCategoryId = 999L;
        existingCategoryId = 1L;


        productDTOWithCategoryDTOEmpty = ProductFactory.createDTOWithoutCategory(existingId);   // ProductDTO com lista de CategoryDTO vazia
        productDTOWithNonExistingCategoryId = ProductFactory                          // ProductDTO com CategoryDTO não existente
            .createDTOWithCategoryDTO(existingId, nonExistingCategoryId);
        productDTOWithCategoryDTO = ProductFactory.                                   // ProductDTO com CategoryDTO existente
            createDTOWithCategoryDTO(existingId, existingCategoryId);


        page = new PageImpl<>(List.of(productDTOWithCategoryDTOEmpty));
    }



//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]


    @Test  //  <findAllPaged> deve <RetornarPage> [quando <>]
    public void findAllPagedShouldReturnPage() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.findAllPaged(Mockito.any()))
            .thenReturn(page); // service.findAllPaged → deve retornar um Page de ProductDTO


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/products/page")
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test  //  <findById> deve <RetornarProductDTO> [quando <IDExistir>]
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.findById(existingId))
            .thenReturn(productDTOWithCategoryDTOEmpty); // service.findById → deve retornar um ProductDTO quando id existir


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/products/{id}", existingId)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists()); // json de resposta tem que ter o campo id
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists()); // json de resposta tem que ter o campo name
    }


    @Test  //  <findById> deve <RetornarStatusNotFound> [quando <IDNaoExistir>]
    public void findByIdShouldReturnStatusNotFoundWhenIdDoesNotExists() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.findById(nonExistingId))
            .thenThrow(EntidadeNotFoundException.class); // service.findById → deve lançar exception quando id não existir


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/products/{id}", nonExistingId)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test  //  <insert> deve <RetornarStatusCreated> [quando <>]
    public void insertShouldReturnStatusCreated() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.insert(Mockito.any()))
            .thenReturn(productDTOWithCategoryDTOEmpty); // service.insert → deve retornar um ProductDTO salvo no banco de dados


//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTOWithCategoryDTOEmpty);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/products")
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists()); // json de resposta tem que ter o campo id
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists()); // json de resposta tem que ter o campo name
        result.andExpect(MockMvcResultMatchers.header().exists("Location")); // Location do header tem que existir
    }


    @Test  //  <insert> deve <RetornarStatusNotFound> [quando <CategoryIdNaoExistir>]
    public void insertShouldReturnStatusNotFoundWhenCategoryIdDoesNotExists() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.insert(Mockito.any()))
            .thenThrow(EntidadeNotFoundException.class); // service.findById → deve lançar exception quando id não existir


//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTOWithNonExistingCategoryId);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/products")
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test  //  <update> deve <RetornarProductDTO> [quando <IdExistir>]
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.update(Mockito.eq(existingId), Mockito.any()))
            .thenReturn(productDTOWithCategoryDTO);// service.update → deve retornar um ProductDTO salvo no banco de dados


//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTOWithCategoryDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/products/{id}", existingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists()); // json de resposta tem que ter o campo id
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists()); // json de resposta tem que ter o campo name
    }


    @Test  //  <update> deve <RetornarStatusNotFound> [quando <IdNaoExistir>]
    public void updateShouldReturnStatusNotFoundWhenIdDoesNotExists() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.update(Mockito.eq(nonExistingId), Mockito.any()))
            .thenThrow(new EntidadeNotFoundException("Product não encontrado: " + nonExistingId)); // service.update → deve lançar exception quando id não existir


//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTOWithCategoryDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/products/{id}", nonExistingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(
            MockMvcResultMatchers.content().string(Matchers.containsString("Product não encontrado")));
    }


    @Test  //  <delete> deve <RetornarStatusNoContent> [quando <IdExistir>]
    public void deleteShouldReturnStatusNoContentWhenIdExists() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.doNothing() // service.delete → não deve retornar nada quando o id existir
            .when(service).delete(existingId);


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/products/{id}", existingId));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(service).delete(existingId); // garante que o método 'service.delete' tenha sido chamado
    }


    @Test  //  <delete> deve <RetornarStatusNotFound> [quando <IdNaoExistir>]
    public void deleteShouldReturnStatusNotFoundWhenIdDoesNotExist() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.doThrow(new EntidadeNotFoundException("Product não encontrado: " + nonExistingId)) // service.delete → deve lançar exception quando id não existir
            .when(service).delete(nonExistingId);


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/products/{id}", nonExistingId));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.message")
            .value("Product não encontrado: " + nonExistingId));

        Mockito.verify(service).delete(nonExistingId); // garante que o método 'service.delete' tenha sido chamado
    }


    @Test  //  <delete> deve <RetornarStatusNotFound> [quando <IdNaoExistir>]
    public void deleteShouldReturnStatusConflictWhenItIsDependentId() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.doThrow(new DatabaseException("Falha de integridade referencial")) // service.delete → deve lançar exception quando id for dependente
            .when(service).delete(dependentId);


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/products/{id}", dependentId));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isConflict());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.message")
            .value("Falha de integridade referencial"));

        Mockito.verify(service).delete(dependentId); // garante que o método 'service.delete' tenha sido chamado
    }
}
