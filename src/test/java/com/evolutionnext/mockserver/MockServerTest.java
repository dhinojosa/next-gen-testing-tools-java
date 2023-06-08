package com.evolutionnext.mockserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerTest {

    private static ClientAndServer mockServer;

    @BeforeAll
    public static void beforeAll() {
        mockServer = startClientAndServer(1080);
    }

    @Test
    void testResponse() throws IOException, InterruptedException {
        try (var mockServerClient = new MockServerClient("localhost", 1080)) {
            mockServerClient
                .when(
                    request()
                        .withMethod("POST")
                        .withPath("/login")
                        .withBody("{username: 'foo', password: 'bar'}")
                )
                .respond(
                    response()
                        .withStatusCode(302)
                        .withCookie(
                            "sessionId", "2By8LOhBmaW5nZXJwcmludCIlMDAzMW"
                        )
                        .withHeader(
                            "Location", "https://www.mock-server.com"
                        )
                );

            HttpClient httpClient = HttpClient.newBuilder().build();

            HttpRequest httpRequest = HttpRequest
                .newBuilder(URI.create("http://localhost:1080/login"))
                .POST(HttpRequest.BodyPublishers.ofString("{username: 'foo', " +
                    "password: 'bar'}"))
                .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());
            System.out.format("Body: %s%n", response.body());
            System.out.format("Status Code: %s%n", response.statusCode());
            System.out.format("Headers: %s%n", response.headers());
        }
    }

    @AfterAll
    static void afterAll() {
        mockServer.stop();
    }
}
