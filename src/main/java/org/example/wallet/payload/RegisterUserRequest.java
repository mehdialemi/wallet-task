package org.example.wallet.payload;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String username;
    private String password;
    private String fullName;
}
