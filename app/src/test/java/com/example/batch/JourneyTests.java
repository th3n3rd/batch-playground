package com.example.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class JourneyTests {

    @Autowired
    private WebTestClient client;

    @Test
    void greetings() {
        join("SZ659218A", "Alice");
        join("WK892520", "Bob");
        greetEverybody();
        listGreetings().contains(
            "Hello Alice",
            "Hello Bob"
        );
    }

    private void join(String niNo, String firstName) {
        client
            .post()
            .uri("/people/")
            .bodyValue(new OnboardingApi.Identity(
                niNo,
                firstName
            ))
            .exchange()
            .expectStatus().isAccepted();
    }

    private void greetEverybody() {
        client
            .post()
            .uri("/people/all/greetings")
            .exchange()
            .expectStatus().isAccepted();
    }

    private WebTestClient.ListBodySpec<Object> listGreetings() {
        return client
            .get()
            .uri("/people/all/greetings")
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk()
            .expectBodyList(Object.class); // for some reason using String.class here does not produce a list of strings!
    }
}
