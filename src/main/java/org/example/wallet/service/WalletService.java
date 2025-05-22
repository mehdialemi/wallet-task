package org.example.wallet.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
import org.example.wallet.repository.WalletRepository;
import org.example.wallet.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    public Wallet getWallet(String owner) throws WalletNotFoundException {
        return walletRepository.findByOwner(owner).orElseThrow(
                () -> new WalletNotFoundException(owner)
        );
    }

    public Wallet createWallet(String owner) {
        Wallet wallet = new Wallet();
        wallet.setOwner(owner);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet addFunds(String owner, BigDecimal amount) throws WalletNotFoundException {
        Wallet wallet = getWallet(owner);
        wallet.setBalance(wallet.getBalance().add(amount));
        transactionRepository.save(new WalletTransaction(owner, "ADD", amount, "Add founds"));
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet withdrawFunds(String owner, BigDecimal amount) throws WalletNotFoundException, InsufficientBalanceException {
        Wallet wallet = getWallet(owner);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        transactionRepository.save(new WalletTransaction(owner, "WITHDRAW", amount, "fund withdrawn"));
        return walletRepository.save(wallet);
    }

}
