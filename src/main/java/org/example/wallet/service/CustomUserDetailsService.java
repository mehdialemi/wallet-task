package org.example.wallet.service;

import org.example.wallet.model.AppUser;
import org.example.wallet.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {

    @Autowired
    private AppUserRepository userRepository;

    public UserDetails loadUserByUsername(String username) {
        AppUser user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        return User.withUsername(user.getUsername()).password(user.getPassword()).roles("USER").build();
    }
}
