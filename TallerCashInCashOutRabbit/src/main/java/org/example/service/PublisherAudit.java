package org.example.service;

import org.example.model.Transaction;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import static org.example.config.RabbitConfig.AUDIT_EXCHANGE;
import static org.example.config.RabbitConfig.AUDIT_ROUTING_KEY;

@Component
public class PublisherAudit {

    private final AmqpTemplate amqpTemplate;

    public PublisherAudit(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }
    public void publishAuditMessage(Transaction transaction) {
        amqpTemplate.convertAndSend(AUDIT_EXCHANGE, AUDIT_ROUTING_KEY, transaction);
    }
}
