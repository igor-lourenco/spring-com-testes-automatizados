package com.expert.testes.controllers;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // O próprio teste sobe o contexto Spring e inicia a aplicação
public class ProductControllerRestAssuredTests {

    private long existingId, nonExistingId;
    private String productName;


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception {
//      Os valores agora têm que ser reais porque vai ser testado o banco de dados

        baseURI = "http://localhost:9200";
        port = 9200;
        existingId = 1L;
        productName = "Ma";

    }

//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]

    @Test //  <findById> deve <RetornarProductDTO> [quando <IdExistir>]
    public void findByIdShouldReturnProductDTOWhenIdExists(){

        RestAssured.given()
            .get("/v1/products/{id}", existingId)
        .then()
            .statusCode(200)
            .body("id", is(Integer.valueOf("" + existingId)))
            .body("name", equalTo("Microwave"))
            .body("imgUrl", equalTo("https://example.com/images/products/microwave.png"))
            .body("price", is(450.0f))
            .body("categories.id", hasItems(1))
            .body("categories.name", hasItems("Home appliances"))
        ;
    }


    @Test //  <findAllPaged> deve <RetornarProductDTO> [quando <ParamNameIsEmpty>]
    public void findAllPagedShouldReturnProductDTOWhenParamNameIsEmpty(){

        RestAssured.given()
            .get("/v1/products/page?page=0&size=5&sort=id,ASC")
        .then()
            .statusCode(200)
            .body("content.id", hasItems(1,2))
            .body("content.name", hasItems("Microwave", "Refrigerator"))
        ;
    }


    @Test //  <findAllPagedProductProjection> deve <RetornarProductDTO> [quando <ParamNameNaoEhEmpty>]
    public void findAllPagedProductProjectionShouldReturnProductDTOWhenParamNameIsDoesNotEmpty(){

        RestAssured.given()
            .get("/v1/products/page/projections?page=0&name={productName}&size=5&sort=id,ASC", productName)
        .then()
            .statusCode(200)
            .body("content.id[0]", is(4))
            .body("content.id[1]", is(13))
            .body("content.name[0]", containsString(productName))
            .body("content.name[1]", containsString(productName))
        ;
    }
}
