package org.example.service;

import org.example.dto.CashRequestDto;
import org.example.dto.TransactionDto;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.model.TransactionType;
import org.example.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class TransactionService {

    private final TransactionRepository repo;
    private final LedgerRequestReplyClient ledgerRequestReplyClient;
    private final PublisherAudit publisherAudit;

    public TransactionService(TransactionRepository repo, LedgerRequestReplyClient ledgerRequestReplyClient,
                              PublisherAudit publisherAudit1) {
        this.repo = repo;
        this.ledgerRequestReplyClient = ledgerRequestReplyClient;
        this.publisherAudit = publisherAudit1;
    }

    public Mono<TransactionDto> cashIn(CashRequestDto req) {
        Transaction trx = Transaction.builder()
                .amount(req.amount())
                .currency(req.currency())
                .type(TransactionType.CASH_IN)
                .status(TransactionStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        return repo.save(trx)
                .flatMap(ledgerRequestReplyClient::sendTransaction)
                .flatMap(transaction -> {
                    trx.setStatus(transaction.getStatus());
                    return repo.save(trx);
                })
                .doOnSuccess(publisherAudit::publishAuditMessage)
                .map(this::toDto);

    }

    public Mono<TransactionDto> cashOut(CashRequestDto req) {
        Transaction trx = Transaction.builder()
                .amount(req.amount())
                .currency(req.currency())
                .type(TransactionType.CASH_OUT)
                .status(TransactionStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        return repo.save(trx)
                .flatMap(ledgerRequestReplyClient::sendTransaction)
                .flatMap(transaction -> {
                    trx.setStatus(transaction.getStatus());
                    return repo.save(trx);
                })
                .doOnSuccess(publisherAudit::publishAuditMessage)
                .map(this::toDto);
    }

    public Mono<TransactionDto> findById(String id) {
        return repo.findById(id)
                .map(this::toDto);
    }

    private TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}