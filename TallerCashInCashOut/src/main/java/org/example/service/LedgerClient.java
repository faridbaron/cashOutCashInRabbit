package org.example.service;

import org.example.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import static org.example.model.TransactionStatus.FAILED;

@Component
public class LedgerClient {

    private final static Logger log = LoggerFactory.getLogger(LedgerClient.class);
    private final WebClient webClient;

    public LedgerClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Transaction> postEntry(Transaction transaction) {
        log.info("POST /ledger/entries - Request Body: {}", transaction);
        return webClient
                .post()
                .uri("/ledger/entries")
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(Transaction.class)
                .retryWhen(Retry.fixedDelay(3, java.time.Duration.ofSeconds(1)))
                .doOnNext(res -> log.info("Response from /ledger/entries: {}", res.getStatus()))
                .doOnError(e -> log.error("Error posting to /ledger/entries ", e))
                .onErrorResume(e -> {
                    transaction.setStatus(FAILED);
                    return Mono.just(transaction);
                });

    }


}