package org.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String LEDGER_EXCHANGE = "ledger.exchange";
    public static final String AUDIT_EXCHANGE = "audit.exchange";
    public static final String LEDGER_QUEUE = "ledger.entry.request.queue";
    public static final String AUDIT_QUEUE = "audit.queue";
    public static final String LEDGER_ROUTING_KEY = "ledger.entry.request";
    public static final String AUDIT_ROUTING_KEY = "audit.entry.queue";
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setReplyTimeout(5000);
        template.setUseTemporaryReplyQueues(true);
        return template;
    }

    @Bean
    public DirectExchange ledgerExchange() {
        return new DirectExchange(LEDGER_EXCHANGE);
    }

    @Bean
    public Queue requestQueue() {
        return new Queue(LEDGER_QUEUE, true);
    }
    @Bean
    public Binding LegderBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(ledgerExchange())
                .with(LEDGER_ROUTING_KEY);
    }
    @Bean
    public DirectExchange auditExchange() {return new DirectExchange(AUDIT_EXCHANGE);}
    @Bean
    public Queue auditQueue() {return new Queue(AUDIT_QUEUE, true);}
    @Bean
    public Binding auditBinding() {
        return BindingBuilder
                .bind(auditQueue())
                .to(auditExchange())
                .with(AUDIT_ROUTING_KEY);
    }


}