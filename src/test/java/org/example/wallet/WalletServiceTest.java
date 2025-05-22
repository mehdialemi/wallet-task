package org.example.wallet;

import org.example.wallet.exception.InsufficientBalanceException;
import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.model.Wallet;
import org.example.wallet.model.WalletTransaction;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

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
    }

    @Test
    public void testAddFunds() throws WalletNotFoundException {
        BigDecimal amount = BigDecimal.valueOf(50);
        Wallet updatedWallet = walletService.addFunds("mehdi", amount);

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
}
