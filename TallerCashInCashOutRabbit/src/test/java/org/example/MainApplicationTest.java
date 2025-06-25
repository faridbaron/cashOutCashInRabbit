package org.example;

import org.example.dto.CashRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MainApplicationTest {
    @Autowired
    WebTestClient client;

    @Test
    void cashInTest() {
        CashRequestDto req = new CashRequestDto(
                BigDecimal.valueOf(100),
                "USD",
                "ext-123"

        );
        client.post().uri("/cash-in")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("POSTED");

    }
    @Test
    void cashOutTest() {
        CashRequestDto req = new CashRequestDto(
                BigDecimal.valueOf(500),
                "USD",
                "ext-123"

        );
        client.post().uri("/cash-out")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("POSTED");

    }
    @Test
    void findByIdTest() {
        String id = "1";
        client.get().uri("/transactions/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }


}
 