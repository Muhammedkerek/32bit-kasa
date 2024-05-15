package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.AdminRepository;
import com.toyota.cashier.DTO.AuthenticationResponse;
import com.toyota.cashier.Domain.Admin;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private PasswordEncoder passwordEncoder;
    private AdminRepository adminRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthenticationService(PasswordEncoder passwordEncoder, AdminRepository adminRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    public AuthenticationResponse register(Admin request) {
        if (adminRepository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse(null, "User already exist");
        }
        Admin admin = new Admin();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode((request.getPassword())));
        admin.setRole(request.getRole());
        admin = adminRepository.save(admin);
        String token = jwtService.generateToken(admin);
        return new AuthenticationResponse(token, "User registration was successful");
    }
    public AuthenticationResponse authenticate(Admin request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        Admin admin = adminRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(admin);

        return new AuthenticationResponse(token , "User login was successful");
    }


}
