package com.toyota.cashier.Resources;

import com.toyota.cashier.DTO.AuthenticationResponse;
import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<AuthenticationResponse> register(@RequestBody Roles request){
        return ResponseEntity.ok(authenticationService.register(request));

    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody Roles response){
        return ResponseEntity.ok(authenticationService.authenticate(response));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Logged out successfully");
    }

}
