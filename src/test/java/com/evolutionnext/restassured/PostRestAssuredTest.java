package com.evolutionnext.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class PostRestAssuredTest {

    public static final int PORT = 1080;

    static MockServerClient mockServer;

    @BeforeAll
    public static void beforeAll() {
        mockServer = startClientAndServer(PORT);
    }

    @BeforeAll
    static void beforeClass() {
        mockServer
            .when(request().withMethod("POST").withPath("/login"))
            .respond(response().withStatusCode(201).withBody("Accepted"));
    }

    @Test
    void testCallingRestContentAsPost() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"foo\", \"password\": \"bar\"}")
            .when()
            .post("http://localhost:1080/login")
            .then()
            .statusCode(201)
            .body(equalTo("Accepted"));
    }

    @AfterAll
    static void afterAll() {
        mockServer.close();
    }
}
