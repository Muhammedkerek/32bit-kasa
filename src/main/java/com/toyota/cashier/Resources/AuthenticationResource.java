package com.toyota.cashier.Resources;

import com.toyota.cashier.DTO.AuthenticationResponse;
import com.toyota.cashier.Domain.Admin;
import com.toyota.cashier.Services.AuthenticationService;
import jakarta.annotation.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationResource {
    private AuthenticationService authenticationService;

    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody Admin request){
        return ResponseEntity.ok(authenticationService.register(request));

    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody Admin response){
        return ResponseEntity.ok(authenticationService.authenticate(response));
    }

}
