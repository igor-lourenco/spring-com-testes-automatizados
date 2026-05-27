package com.expert.testes.controllers;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.services.ProductService;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.utils.ProductFactory;
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

    private ProductDTO productDTOWithCategoryDTOEmpty;       // ProductDTO com lista de CategoryDTO vazia
    private PageImpl<ProductDTO> page;
    private long existingId;
    private long nonExistingId;


    @Autowired
    private MockMvc mockMvc; // serve para simular requisições HTTP sem a necessidade de subir um servidor web real (como o Tomcat)

    @MockitoBean // Usa quando a classe de teste carrega o contexto da aplicação e precisa mockar algum bean do sistema.
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 999L;

        productDTOWithCategoryDTOEmpty = ProductFactory.createDTOWithoutCategory();   // ProductDTO com lista de CategoryDTO vazia

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
}
