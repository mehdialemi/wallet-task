package org.example.wallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.AuditLog;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
import org.example.wallet.repository.AuditLogRepository;
import org.example.wallet.repository.WalletRepository;
import org.example.wallet.repository.WalletTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;

    private final WalletTransactionRepository transactionRepository;

    private final AuditLogRepository auditLogRepository;

    public Wallet getWallet(String owner) throws WalletNotFoundException {
        return walletRepository.findByOwner(owner).orElseThrow(
                () -> new WalletNotFoundException(owner)
        );
    }

    public BigDecimal getBalance(String owner) throws WalletNotFoundException {
        Wallet wallet = getWallet(owner);
        return wallet.getBalance();
    }

    public Wallet createWallet(String owner) {
        Wallet wallet = new Wallet();
        wallet.setOwner(owner);
        return walletRepository.save(wallet);
    }

    @Transactional(rollbackFor = WalletNotFoundException.class,
            propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Wallet deposit(String owner, BigDecimal amount) throws WalletNotFoundException {
        Wallet wallet = getWallet(owner);
        wallet.setBalance(wallet.getBalance().add(amount));
        transactionRepository.save(new WalletTransaction(owner, "ADD", amount, "Add founds"));
        return walletRepository.save(wallet);
    }

    @Transactional(rollbackFor = {InsufficientBalanceException.class},
            propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Wallet withdrawFunds(String owner, BigDecimal amount) throws WalletNotFoundException, InsufficientBalanceException {
        Wallet wallet = getWallet(owner);
        if (wallet.getBalance().compareTo(amount) < 0) {
            logFailedTransaction(owner, "WITHDRAW", amount,
                    "Withdrawal failed: Insufficient balance");
            throw new InsufficientBalanceException("Insufficient Balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        transactionRepository.save(new WalletTransaction(owner, "WITHDRAW", amount, "fund withdrawn"));
        return walletRepository.save(wallet);
    }

    @Transactional(rollbackFor = {InsufficientBalanceException.class},
            propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void transfer(String fromOwner, String toOwner, BigDecimal amount)
            throws WalletNotFoundException, InsufficientBalanceException {
        Wallet sender = getWallet(fromOwner);
        Wallet receiver = getWallet(toOwner);

        if (sender.getBalance().compareTo(amount) < 0) {
            logFailedTransaction(fromOwner, "TRANSFER", amount,
                    "Transfer failed: Insufficient funds to transfer to " + toOwner);
            throw new InsufficientBalanceException("Insufficient funds to transfer");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        transactionRepository.save(new WalletTransaction(fromOwner, "TRANSFER", amount.negate(), "Transfer to " + toOwner));
        transactionRepository.save(new WalletTransaction(toOwner, "TRANSFER", amount, "Received from " + fromOwner));

        walletRepository.save(sender);
        walletRepository.save(receiver);
    }

    public List<WalletTransaction> getTransactionHistory(String owner) {
        return transactionRepository.findByOwner(owner);
    }

    public Page<WalletTransaction> getTransactionHistory(String owner, int page, int size, boolean descending) {
        Sort sort = descending ?
                Sort.by("timestamp").descending() : Sort.by("timestamp").ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return transactionRepository.findByOwner(owner, pageable);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void logFailedTransaction(String owner, String type, BigDecimal amount, String reason) {
        log.warn("Logging failed transaction for {}: {} {} - {}", owner, type, amount, reason);
        WalletTransaction failedTx = new WalletTransaction(owner, type, amount, reason);
        transactionRepository.save(failedTx);
        auditLogRepository.save(new AuditLog(owner, type, amount, reason));
    }
}
