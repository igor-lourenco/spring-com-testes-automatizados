package com.expert.testes.controllers;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.repositories.ProductRepository;
import com.expert.testes.utils.ProductFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest // Carrega o contexto da aplicação (teste de integração)
@AutoConfigureMockMvc // serve para testar API sem subir o servidor de verdade (sem Tomcat rodando na porta, por exemplo)
@Transactional //  para dar rollback automático após cada teste e isolamento total
public class ProductControllerIntegrationTests {

    private long existingId;
    private long nonExistingId;
    private long nonExistingCategoryId;
    private long existingCategoryId;


    @Autowired
    private MockMvc mockMvc; // serve para simular requisições HTTP sem a necessidade de subir um servidor web real (como o Tomcat)

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository repository;

    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception {
//      Os valores agora têm que ser reais porque vai ser testado o banco de dados
        existingId = 1L;
        nonExistingId = 999L;
        nonExistingCategoryId = 999L;
        existingCategoryId = 1L;

    }



//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]


    @Test  //  <insert> deve <RetornarStatusCreated> [quando <>]
    public void insertShouldReturnStatusCreated() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        ProductDTO productDTO = ProductFactory.createDTOWithCategoryDTO(null, existingCategoryId);

        String expectedName = productDTO.name();
        String expectedDescription = productDTO.description();
        long countTotalProducts = repository.count();


//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/products")
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists()); // json de resposta tem que ter o campo id
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedName));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedDescription));
        result.andExpect(MockMvcResultMatchers.header().exists("Location")); // Location do header tem que existir

        //      opcional: para garantir se o product foi realmente inserido do banco
        Assertions.assertEquals(countTotalProducts + 1 , repository.count());
        Assertions.assertTrue(repository.existsById(existingId));
    }


    @Test  //  <insert> deve <RetornarStatusNotFound> [quando <CategoryIdNaoExistir>]
    public void insertShouldReturnStatusNotFoundWhenCategoryIdDoesNotExist() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        int expectedStatus = 404;
        String expectedError = "Recurso não encontrado";
        String expectedMessage = "Category não encontrado";

        ProductDTO productDTO = ProductFactory.createDTOWithCategoryDTO(null, nonExistingCategoryId);

//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/products")
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(expectedStatus));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(expectedError));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.message")
            .value(Matchers.containsString(expectedMessage)));
    }


    @Test  //  <update> deve <RetornarProductDTO> [quando <IdExistir>]
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        ProductDTO productDTO = ProductFactory.createDTOWithCategoryDTO(null, existingCategoryId);

        String expectedName = productDTO.name();
        String expectedDescription = productDTO.description();


//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/products/{id}", existingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingId));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedName));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedDescription));
    }


    @Test  //  <update> deve <RetornarStatusNotFound> [quando <IdNaoExistir>]
    public void updateShouldReturnStatusNotFoundWhenIdDoesNotExist() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        ProductDTO productDTO = ProductFactory.createDTOWithCategoryDTO(null, existingCategoryId);
        int expectedStatus = 404;
        String expectedError = "Recurso não encontrado";
        String expectedMessage = "Product não encontrado";

//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/products/{id}", nonExistingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(expectedStatus));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(expectedError));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.message")
            .value(Matchers.containsString(expectedMessage)));
    }


    @Test  //  <update> deve <RetornarStatusNotFound> [quando <CategoryIdNaoExistir>]
    public void updateShouldReturnStatusNotFoundWhenCategoryIdDoesNotExist() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        int expectedStatus = 404;
        String expectedError = "Recurso não encontrado";
        String expectedMessage = "Category não encontrado";

        ProductDTO productDTO = ProductFactory.createDTOWithCategoryDTO(existingId, nonExistingCategoryId);

//      -> Act: execute as ações necessárias
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/products/{id}", existingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(expectedStatus));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(expectedError));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.message")
            .value(Matchers.containsString(expectedMessage)));
    }


    @Test  //  <delete> deve <RetornarStatusNoContent> [quando <IdExistir>]
    public void deleteShouldReturnStatusNoContentWhenIdExist() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        long countTotalProducts = repository.count();


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/products/{id}", existingId)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNoContent());
        result.andExpect(MockMvcResultMatchers.content().string(""));

//      opcional: para garantir se o product foi realmente deletado do banco
        Assertions.assertEquals(countTotalProducts - 1 , repository.count());
        Assertions.assertFalse(repository.existsById(existingId));
    }


    @Test  //  <delete> deve <RetornarStatusNotFound> [quando <IdNaoExistir>]
    public void deleteShouldReturnStatusNotFoundWhenIdDoesNotExist() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        int expectedStatus = 404;
        String expectedError = "Recurso não encontrado";
        String expectedMessage = "Product não encontrado";


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/products/{id}", nonExistingId)
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(expectedStatus));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(expectedError));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.message")
            .value(Matchers.containsString(expectedMessage)));


    }
}
