package org.example.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wallet.dto.AddFundRequest;
import org.example.wallet.dto.TransferRequest;
import org.example.wallet.dto.WithdrawFundRequest;
import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
import org.example.wallet.service.WalletService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "wallet balance", description = "Get wallet balance of the current user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("balance")
    public BigDecimal balance() throws WalletNotFoundException {
        return service.getBalance(getCurrentUsername());
    }

    @Operation(summary = "Withdraw from wallet")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("withdraw")
    public Wallet withdraw(@RequestBody WithdrawFundRequest request)
            throws WalletNotFoundException, InsufficientBalanceException {
        return service.withdrawFunds(getCurrentUsername(), request.getAmount());
    }

    @Operation(summary = "Add funds to wallet",
            description = "Simply add the provided value to the current user wallet ")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("deposit")
    public Wallet deposit(@RequestBody AddFundRequest request)
            throws WalletNotFoundException {
        return service.deposit(getCurrentUsername(), request.getAmount());
    }

    @Operation(summary = "Transfer money between wallet",
            description = "Transfer money from the current user to the provided account username according to the given request")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("transfer")
    public String transfer(@RequestBody TransferRequest request) throws WalletNotFoundException, InsufficientBalanceException {
        service.transfer(getCurrentUsername(), request.getToUser(), request.getAmount());
        return "Transfer successful";
    }

    @Operation(summary = "Transaction history",
            description = "Get the list of transaction for the wallet of the current user")
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
