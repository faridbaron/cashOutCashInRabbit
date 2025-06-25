package org.example.handler;

import org.example.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static org.example.config.RabbitConfig.AUDIT_QUEUE;

@Component
public class AuditConsumer {

    private final static Logger log = LoggerFactory.getLogger(AuditConsumer.class);
    @RabbitListener(queues = AUDIT_QUEUE)
    public void receiveAuditMessage(Transaction tx) {
        log.info("Audit message received: {}", tx);
    }
}
