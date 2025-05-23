package org.example.wallet.exception;

public class InsufficientBalanceException extends WalletException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
