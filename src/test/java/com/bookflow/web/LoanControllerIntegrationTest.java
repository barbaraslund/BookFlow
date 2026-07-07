package com.bookflow.web;

import com.bookflow.dto.BookResponse;
import com.bookflow.dto.LoanRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoanControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loaningAndReturningABook_updatesInventoryCorrectly() throws Exception {
        long bookId = 5L; // "Design Patterns" - seeded with totalCopies=3, availableCopies=3, no active loans
        long memberId = 1L;

        assertAvailableCopies(bookId, 3);

        long loanId = createLoan(bookId, memberId);

        assertAvailableCopies(bookId, 2);

        webTestClient.post().uri("/api/loans/{loanId}/return", loanId)
                .header("X-Member-Id", String.valueOf(memberId))
                .exchange()
                .expectStatus().isOk();

        assertAvailableCopies(bookId, 3);
    }

    @Test
    void gettingAnUnknownBook_returns404() {
        webTestClient.get().uri("/api/books/{id}", 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    private long createLoan(long bookId, long memberId) throws Exception {
        byte[] body = webTestClient.post().uri("/api/loans")
                .header("X-Member-Id", String.valueOf(memberId))
                .bodyValue(new LoanRequest(bookId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        Map<String, Object> json = objectMapper.readValue(body, new TypeReference<>() {});
        return ((Number) json.get("id")).longValue();
    }

    private void assertAvailableCopies(long bookId, int expected) {
        webTestClient.get().uri("/api/books/{id}", bookId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class)
                .value(book -> assertThat(book.availableCopies()).isEqualTo(expected));
    }
}
