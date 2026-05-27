package com.expert.testes.controllers;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.services.ProductService;
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

import java.util.List;

@WebMvcTest(ProductController.class) // Carrega o contexto, porém somente da camada web (teste de unidade: controlador)
public class ProductControllerTests {

    private ProductDTO productDTOWithCategoryDTOEmpty;       // ProductDTO com lista de CategoryDTO vazia
    private PageImpl<ProductDTO> page;


    @Autowired
    private MockMvc mockMvc; // serve para simular requisições HTTP sem a necessidade de subir um servidor web real (como o Tomcat)

    @MockitoBean // Usa quando a classe de teste carrega o contexto da aplicação e precisa mockar algum bean do sistema.
    private ProductService service;


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception {
        productDTOWithCategoryDTOEmpty = ProductFactory.createDTOWithoutCategory();   // ProductDTO com lista de CategoryDTO vazia

        page = new PageImpl<>(List.of(productDTOWithCategoryDTOEmpty));
    }



    //	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]


    @Test  //  <findAllPaged> deve <RetornarPage> [quando <>]
    public void findAllPagedShouldReturnPage() throws Exception {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(service.findAllPaged(Mockito.any())).thenReturn(page); // service.findAllPaged → deve retornar um Page de ProductDTO


//      -> Act: execute as ações necessárias
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/products/page")
            .accept(MediaType.APPLICATION_JSON));


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
