package org.example.service;

import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import static org.example.config.RabbitConfig.LEDGER_EXCHANGE;
import static org.example.config.RabbitConfig.LEDGER_ROUTING_KEY;

@Component
public class LedgerRequestReplyClient {
    private final static Logger log = LoggerFactory.getLogger(LedgerRequestReplyClient.class);
    private final RabbitTemplate rabbitTemplate;

    public LedgerRequestReplyClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<Transaction> sendTransaction(Transaction tx) {
        return Mono.fromCallable(() ->
                        (Transaction) rabbitTemplate.convertSendAndReceive(LEDGER_EXCHANGE, LEDGER_ROUTING_KEY, tx))
                .doOnNext(transaction -> log.info("Response from ledger with Rabbit: {}", transaction.getStatus()))
                .retryWhen(Retry.fixedDelay(3, java.time.Duration.ofSeconds(1)))
                .onErrorResume(e -> {
                    log.error("Error during RabbitMQ communication: ", e);
                    tx.setStatus(TransactionStatus.FAILED);
                    return Mono.just(tx);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}

