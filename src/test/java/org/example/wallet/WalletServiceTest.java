package org.example.wallet;

import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
import org.example.wallet.repository.AuditLogRepository;
import org.example.wallet.repository.WalletRepository;
import org.example.wallet.repository.WalletTransactionRepository;
import org.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setOwner("mehdi");
        wallet.setBalance(BigDecimal.valueOf(100));
        when(walletRepository.findByOwner("mehdi")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);
    }

    @Test
    public void testAddFunds() throws WalletNotFoundException {
        BigDecimal amount = BigDecimal.valueOf(50);
        Wallet updatedWallet = walletService.deposit("mehdi", amount);

        assertEquals(BigDecimal.valueOf(150), updatedWallet.getBalance());
        verify(transactionRepository).save(any(WalletTransaction.class));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testWithdrawFunds_Success()
            throws WalletNotFoundException, InsufficientBalanceException {
        BigDecimal amount = BigDecimal.valueOf(30);
        Wallet updatedWallet = walletService.withdrawFunds("mehdi", amount);

        assertEquals(BigDecimal.valueOf(70), updatedWallet.getBalance());
        verify(transactionRepository).save(any(WalletTransaction.class));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testWithdrawFunds_InsufficientBalance() {
        BigDecimal amount = BigDecimal.valueOf(200);

        InsufficientBalanceException exception =
                assertThrows(InsufficientBalanceException.class, () -> {
                    walletService.withdrawFunds("mehdi", amount);
                });

        assertEquals("Insufficient Balance", exception.getMessage());
    }

    @Test
    public void testTransferFunds_Success() throws WalletNotFoundException, InsufficientBalanceException {
        Wallet targetWallet = new Wallet();
        targetWallet.setId(2L);
        targetWallet.setOwner("saeed");
        targetWallet.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findByOwner("saeed")).thenReturn(Optional.of(targetWallet));

        walletService.transfer("mehdi", "saeed", BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), walletService.getWallet("mehdi").getBalance());
        assertEquals(BigDecimal.valueOf(80), walletService.getWallet("saeed").getBalance());

        verify(transactionRepository, times(2)).save(any(WalletTransaction.class));
        verify(walletRepository).save(wallet);
        verify(walletRepository).save(targetWallet);
    }

    @Test
    public void testTransferFunds_InsufficientBalance() {
        Wallet targetWallet = new Wallet();
        targetWallet.setId(2L);
        targetWallet.setOwner("saeed");
        targetWallet.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findByOwner("saeed")).thenReturn(Optional.of(targetWallet));

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            walletService.transfer("mehdi", "saeed", BigDecimal.valueOf(200));
        });

        assertEquals("Insufficient funds to transfer", exception.getMessage());
    }
}
