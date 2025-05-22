package org.example.wallet.service;

import org.example.wallet.model.AppUser;
import org.example.wallet.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService {

    @Autowired
    private AppUserRepository userRepository;

    public UserDetails loadUserByUsername(String username) {
        AppUser user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        return new User(user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
