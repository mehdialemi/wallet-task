package org.example.wallet.controller;

import org.example.wallet.model.AppUser;
import org.example.wallet.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(@RequestParam String username, @RequestParam String password) {
        if (userRepository.existsById(username)) {
            return "username already exists";
        }

        AppUser user = new AppUser(username, passwordEncoder.encode(password));
        userRepository.save(user);
        return "User registered successfully";
    }
}
