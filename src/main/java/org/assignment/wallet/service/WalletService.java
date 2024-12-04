package org.assignment.wallet.service;

import org.assignment.wallet.dto.WalletBalanceDTO;
import org.assignment.wallet.exception.ResourceNotFoundException;
import org.assignment.wallet.mapper.WalletMapper;
import org.assignment.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Cacheable("wallets")
    public boolean walletExists(UUID walletId) {
        return walletRepository.existsById(walletId);
    }

    @Transactional(readOnly = true)
    public WalletBalanceDTO getWalletBalance(UUID walletId) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet with id " + walletId + " not found"));
        return walletMapper.map(wallet);
    }
}
