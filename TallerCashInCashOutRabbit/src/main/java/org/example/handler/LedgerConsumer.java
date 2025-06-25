package org.example.handler;

import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static org.example.config.RabbitConfig.LEDGER_QUEUE;

@Component
public class LedgerConsumer {
    private final static Logger log = LoggerFactory.getLogger(LedgerConsumer.class);
    @RabbitListener(queues = LEDGER_QUEUE)
    public Transaction receive(Transaction tx) {
        log.info("Event received: {}", tx);
        tx.setStatus(TransactionStatus.POSTED);
        tx.setCreatedAt(Instant.now());
        return tx;
    }
}