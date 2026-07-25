package com.expert.testes.utils;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TokenUtil {

    @Value("${security.teste.client-id:myclientid}") // passando o valor diretamente na anotação
    private String clientId;
    @Value("${security.test.client-secret:myclientsecret}") // passando o valor diretamente na anotação
    private String clientSecret;

    public String obtainAccessToken(MockMvc mockMvc, String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        params.add("scope", "READ WRITE");

        ResultActions result = mockMvc
            .perform(post("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
                .with(httpBasic(clientId, clientSecret)) // Authorization Basic ...
                .headers(httpHeaders -> {
                    httpHeaders.add("username", username);
                    httpHeaders.add("password", password);
                })
                .accept("application/json;charset=UTF-8"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    public static String obtainAccessToken(String username, String password) throws Exception {

        String clientId = "myclientid";
        String clientSecret = "myclientsecret";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
        params.add("scope", "READ WRITE");

        Response response = RestAssured.given()
            .auth()
            .preemptive()
            .basic(clientId, clientSecret)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED.toString())
            .formParam("grant_type", AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())
            .formParam("scope", "READ WRITE")
            .header("username", username)
            .header("password", password)
          .when()
            .post("/oauth2/token");

        JsonPath jsonBody = response.jsonPath();
        String token = jsonBody.getString("access_token");
        return token;
    }
}
