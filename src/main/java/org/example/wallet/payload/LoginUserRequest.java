package org.example.wallet.payload;

import lombok.Data;

@Data
public class LoginUserRequest {
    private String username;
    private String password;
}
