package org.example.wallet.controller;

import org.example.wallet.exception.WalletNotFoundException;
import org.example.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService service;

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return authentication.getName();
    }
    @GetMapping("/balance")
    private ResponseEntity<BigDecimal> balance(@RequestParam String owner) throws WalletNotFoundException {
        BigDecimal balance = service.getBalance(owner);
        return ResponseEntity.ok(balance);
    }
}
