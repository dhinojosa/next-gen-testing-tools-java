package com.evolutionnext.restassured;

import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;

import java.util.Map;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class RestAssuredTest {

    private static final String data = """
        {
           "lotto":{
              "lottoId":5,
              "winning-numbers":[2,45,34,23,7,5,3],
              "winners":[
                 {
                    "winnerId":23,
                    "numbers":[2,45,34,23,3,5]
                 },
                 {
                    "winnerId":54,
                    "numbers":[52,3,12,11,18,22]
                 }
              ]
           }
        }""";
    public static final int PORT = 1080;

    static MockServerClient mockServer;

    @BeforeAll
    public static void beforeAll() {
        mockServer = startClientAndServer(PORT);
    }

    @BeforeAll
    static void beforeClass() {
        mockServer
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/lotto/5")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeader(
                        "Location",
                        "https://www.mock-server.com"
                    )
                    .withContentType(MediaType.JSON_UTF_8)
                    .withBody(data));
    }

    @Test
    void testCallingRestContent() {
        when()
            .get("http://localhost:1080/lotto/{id}", 5)
            .then()
            .statusCode(200)
            .body("lotto.lottoId", equalTo(5),
                "lotto.winners.winnerId", hasItems(23, 54));
    }

    @Test
    void testCallingRestContentWithExtractAs() {
        Map<String, Object> result = when()
            .get("http://localhost:1080/lotto/{id}", 5)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

        System.out.println(result.get("lotto"));
    }

    @AfterAll
    static void afterAll() {
        mockServer.close();
    }
}
