package org.assignment.wallet.controller;

import jakarta.validation.Valid;
import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.dto.WalletBalanceDTO;
import org.assignment.wallet.service.TransactionService;
import org.assignment.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @GetMapping("/{WALLET_UUID}")
    public ResponseEntity<WalletBalanceDTO> getWalletBalance(@PathVariable(name = "WALLET_UUID") UUID walletId) {
        var walletBalanceDto = walletService.getWalletBalance(walletId);
        return ResponseEntity.ok().body(walletBalanceDto);
    }

    @PostMapping
    public ResponseEntity<Void> processTransaction(@Valid @RequestBody TransactionRequestDTO request) {
        transactionService.processTransaction(request);
        return ResponseEntity.ok().build();
    }

}
