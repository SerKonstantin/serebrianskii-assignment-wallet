package org.assignment.wallet.service;

import org.assignment.wallet.dto.TransactionRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.assignment.wallet.rabbitmq.BalanceChangeSend;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;

@Service
public class TransactionService {

    @Autowired
    private BalanceChangeSend balanceChangeProducer;

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

    @Transactional
    public void processTransaction(TransactionRequestDTO request) {
        transactionCounter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            balanceChangeProducer.sendTransaction(request);
        } catch (Exception e) {
            errorCounter.increment();
            throw e;
        } finally {
            sample.stop(transactionTimer);
        }
    }
}
