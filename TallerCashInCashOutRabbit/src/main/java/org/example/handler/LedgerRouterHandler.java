package org.example.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class LedgerRouterHandler {

    @Bean
    public RouterFunction<ServerResponse> router() {
        return RouterFunctions
                .route(POST("/ledger/entries"), this::createEntry);
    }

    private Mono<ServerResponse> createEntry(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(String.class)
                .flatMap(body -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "POSTED");
                    result.put("entryId", UUID.randomUUID().toString());
                    result.put("timestamp", Instant.now());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(result);
                });
    }
}