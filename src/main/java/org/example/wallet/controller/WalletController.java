package org.example.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
import org.example.wallet.service.WalletService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet")
public class WalletController {

    private final WalletService service;

    @Operation(summary = "Create wallet for registered user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("create")
    public Wallet createWallet() {
        return service.createWallet(getCurrentUsername());
    }

    @Operation(summary = "Get balance of the current user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("balance")
    public BigDecimal balance(@RequestParam String owner) throws WalletNotFoundException {
        return service.getBalance(owner);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("transfer")
    public String transfer(@RequestParam String to, @RequestParam BigDecimal amount) throws WalletNotFoundException, InsufficientBalanceException {
        service.transfer(getCurrentUsername(), to, amount);
        return "Transfer successful";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("history")
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
