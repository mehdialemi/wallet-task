package org.example.wallet.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WalletNotFoundException extends Exception {
    private final String owner;

}
