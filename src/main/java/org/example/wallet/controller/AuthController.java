package org.example.wallet.controller;

import lombok.RequiredArgsConstructor;
import org.example.wallet.model.AppUser;
import org.example.wallet.payload.LoginResponse;
import org.example.wallet.payload.LoginUserRequest;
import org.example.wallet.payload.RegisterUserRequest;
import org.example.wallet.service.AuthenticationService;
import org.example.wallet.service.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("register")
    public AppUser register(@RequestBody RegisterUserRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("login")
    public LoginResponse authenticate(@RequestBody LoginUserRequest request) {
        AppUser user = authenticationService.authenticate(request);
        String jwtToken = jwtService.generateToken(user);
        LoginResponse response = new LoginResponse();
        response.setToken(jwtToken);
        response.setExpiresIn(jwtService.getExpirationTime());

        return response;
    }
}
