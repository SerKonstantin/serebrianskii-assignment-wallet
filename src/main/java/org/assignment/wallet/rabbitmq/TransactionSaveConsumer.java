package org.assignment.wallet.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.assignment.wallet.model.Transaction;
import org.assignment.wallet.repository.TransactionRepository;
import org.assignment.wallet.mapper.TransactionMapper;
import org.assignment.wallet.dto.TransactionRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Service
public class TransactionSaveConsumer {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @RabbitListener(queues = "transactionSaveQueue")
    public void saveTransaction(TransactionRequestDTO transactionRequest) {
        // Map and save the transaction to the database
        Transaction transaction = transactionMapper.map(transactionRequest);
        transaction.setId(UUID.randomUUID());
        transactionRepository.save(transaction);
    }
}
