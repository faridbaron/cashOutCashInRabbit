package org.example.handler;

import org.example.dto.CashRequestDto;
import org.example.service.TransactionService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TransactionHandler {

    public final TransactionService service;

    public TransactionHandler(TransactionService service) {
        this.service = service;
    }

    public Mono<ServerResponse> cashIn(ServerRequest request) {
        return request.bodyToMono(CashRequestDto.class)
                .flatMap(service::cashIn)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }

    public Mono<ServerResponse> cashOut(ServerRequest request) {
        return request.bodyToMono(CashRequestDto.class)
                .flatMap(service::cashOut)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        return service.findById(request.pathVariable("id"))
                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}