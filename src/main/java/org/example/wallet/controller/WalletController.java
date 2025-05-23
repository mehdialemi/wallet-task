package org.example.wallet.controller;

import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
import org.example.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService service;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/create")
    public Wallet createWallet() {
        return service.createWallet(getCurrentUsername());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/balance")
    public BigDecimal balance(@RequestParam String owner) throws WalletNotFoundException {
        BigDecimal balance = service.getBalance(owner);
        return balance;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/transfer")
    public String transfer(@RequestParam String to, @RequestParam BigDecimal amount) throws WalletNotFoundException, InsufficientBalanceException {
        service.transfer(getCurrentUsername(), to, amount);
        return "Transfer successful";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/history")
    public List<WalletTransaction> transactionHistory() {
        return service.getTransactionHistory(getCurrentUsername());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return authentication.getName();
    }
}
