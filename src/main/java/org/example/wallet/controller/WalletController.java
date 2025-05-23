package org.example.wallet.controller;

import ch.qos.logback.core.util.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.example.wallet.dto.AddFundRequest;
import org.example.wallet.dto.ScheduleRequest;
import org.example.wallet.dto.TransferRequest;
import org.example.wallet.dto.WithdrawFundRequest;
import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
import org.example.wallet.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Operation(summary = "All transaction history",
            description = "Get list all of transactions for the wallet of the current user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("history")
    public List<WalletTransaction> transactionHistory() {
        return service.getTransactionHistory(getCurrentUsername());
    }

    @Operation(summary = "Pageable transaction history",
            description = "Get list of transaction considering page index and size")
    @GetMapping("transactions")
    public Page<WalletTransaction> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean descending,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        if (from != null && to != null) {
            LocalDateTime fromDateTime = LocalDateTime.parse(from);
            LocalDateTime toDateTime = LocalDateTime.parse(to);
            return service.getTransactionHistory(getCurrentUsername(), page, size, descending, fromDateTime, toDateTime);
        }
        return service.getTransactionHistory(getCurrentUsername(), page, size, descending);
    }

    @Operation(summary = "Schedule wallet operation",
            description = "Schedule deposit, withdraw, and transfer operation in designated datetime")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("schedule")
    public String scheduleTransaction(@RequestBody ScheduleRequest request) throws BadRequestException {
        if (!StringUtil.isNullOrEmpty(request.getScheduledTime())) {
            LocalDateTime localDateTime = LocalDateTime.parse(request.getScheduledTime());
            service.scheduleTransaction(getCurrentUsername(),
                    request.getTargetOwner(), request.getAmount(), request.getType(), localDateTime);
            return "Scheduled successfully";
        }
        throw new BadRequestException("invalid request for scheduled transaction");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return authentication.getName();
    }
}
