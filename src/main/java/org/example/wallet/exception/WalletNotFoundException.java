package org.example.wallet.exception;

import lombok.Getter;

@Getter
public class WalletNotFoundException extends WalletException {
    private final String owner;

    public WalletNotFoundException(String owner) {
        this(owner, "");
    }

    public WalletNotFoundException(String owner, String message) {
        super(message);
        this.owner = owner;
    }
}
