package org.assignment.wallet.service;

import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.exception.InvalidRequestArgumentException;
import org.assignment.wallet.mapper.TransactionMapper;
import org.assignment.wallet.repository.TransactionRepository;
import org.assignment.wallet.repository.WalletRepository;
import org.assignment.wallet.event.TransactionCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.ConcurrentModificationException;

@Service
public class TransactionService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final MeterRegistry meterRegistry;
    private final Timer transactionTimer;
    private final Counter transactionCounter;
    private final Counter errorCounter;

    public TransactionService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.transactionTimer = Timer.builder("wallet.transaction.duration")
            .description("Time taken to process transaction")
            .register(meterRegistry);
        this.transactionCounter = Counter.builder("wallet.transaction.total")
            .description("Total number of transactions")
            .register(meterRegistry);
        this.errorCounter = Counter.builder("wallet.transaction.errors")
            .description("Number of failed transactions")
            .register(meterRegistry);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processTransaction(TransactionRequestDTO request) {
        transactionCounter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            int maxRetries = 3;
            int attempt = 0;
            while (attempt < maxRetries) {
                try {
                    processTransactionInternal(request);
                    break;
                } catch (ObjectOptimisticLockingFailureException e) {
                    attempt++;
                    if (attempt == maxRetries) {
                        throw new ConcurrentModificationException("Maximum retry attempts reached");
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread was interrupted", ie);
                    }
                }
            }
        } catch (Exception e) {
            errorCounter.increment();
            throw e;
        } finally {
            sample.stop(transactionTimer);
        }
    }

    private void processTransactionInternal(TransactionRequestDTO request) {
        transactionTimer.record(() -> {
            var walletId = request.getWalletId();
            var wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new InvalidRequestArgumentException(
                        "Wallet with id " + walletId + " not found"
                        ));

            var newBalance = calculateBalance(wallet.getBalance(), request);
            wallet.setBalance(newBalance);
            try {
                walletRepository.save(wallet);
            } catch (ObjectOptimisticLockingFailureException e) {
                throw new ConcurrentModificationException("Please retry transaction");
            }

            eventPublisher.publishEvent(new TransactionCreatedEvent(request, wallet));
        });
    }

    private BigDecimal calculateBalance(BigDecimal currentBalance, TransactionRequestDTO request) {
        var amount = request.getAmount();
        switch (request.getOperationType()) {
            case DEPOSIT:
                return currentBalance.add(amount);
            case WITHDRAW:
                if (currentBalance.compareTo(amount) < 0) {
                    throw new InvalidRequestArgumentException("Недостаточно средств.");
                }
                return currentBalance.subtract(amount);
            default:
                throw new InvalidRequestArgumentException(
                        "Сервер не смог распознать тип операции: " + request.getOperationType()
                );
        }
    }
}

@Component
class TransactionEventListener {
    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Async
    @EventListener
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        var transaction = transactionMapper.map(event.getRequest());
        transaction.setId(UUID.randomUUID());
        transaction.setWallet(event.getWallet());
        transactionRepository.save(transaction);
    }
}
