package org.example.config;

import org.example.handler.TransactionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(TransactionHandler handler) {
        return RouterFunctions.route()
                .POST("/cash-in", handler::cashIn)
                .POST("/cash-out", handler::cashOut)
                .GET("/transactions/{id}", handler::findById)
                .build();
    }

}