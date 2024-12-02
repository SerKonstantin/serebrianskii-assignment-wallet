package org.assignment.wallet.service;

import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.exception.InvalidRequestArgumentException;
import org.assignment.wallet.mapper.TransactionMapper;
import org.assignment.wallet.repository.TransactionRepository;
import org.assignment.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public void processTransaction(TransactionRequestDTO request) {
        var walletId = request.getWalletId();
        var wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new InvalidRequestArgumentException("Wallet with id " + walletId + " not found"));

        var newBalance = calculateBalance(wallet.getBalance(), request);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        var transaction = transactionMapper.map(request);
        transaction.setId(UUID.randomUUID());
        transaction.setWallet(wallet);
        transactionRepository.save(transaction);
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
