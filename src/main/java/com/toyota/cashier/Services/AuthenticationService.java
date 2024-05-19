package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.AdminRepository;
import com.toyota.cashier.DAO.TokenRepository;
import com.toyota.cashier.DTO.AuthenticationResponse;
import com.toyota.cashier.Domain.Admin;
import com.toyota.cashier.Domain.Token;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuthenticationService {
    private PasswordEncoder passwordEncoder;
    private AdminRepository adminRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private TokenRepository tokenRepository;

    public AuthenticationService(PasswordEncoder passwordEncoder,
                                 AdminRepository adminRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 TokenRepository tokenRepository) {

        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
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
        String jwt = jwtService.generateToken(admin);
        SaveUserToken(jwt, admin);

        return new AuthenticationResponse(jwt, "User registration was successful");
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
        revokeAllTokenByUser(admin);
        SaveUserToken(token, admin);
        return new AuthenticationResponse(token, "User login was successful");
    }


    private void SaveUserToken(String jwt, Admin admin) {
        Token token = new Token();
        token.setToken(jwt);
        token.setAdmin(admin);
        token.setLoggedOut(false);
        tokenRepository.save(token);
    }




    public void revokeAllTokenByUser(Admin admin){
        List<Token> validateTokenListByUser = tokenRepository.findAllUsersById(admin.getId());
        if(!validateTokenListByUser.isEmpty()){
            validateTokenListByUser.forEach(t ->{
                t.setLoggedOut(true);
            } );
        }
        tokenRepository.saveAll(validateTokenListByUser);
    }






   /* private void deletePreviousTokens(Admin admin) {
        List<Token> tokens = tokenRepository.findAllUsersById(admin.getId());
        if (!tokens.isEmpty()) {
            tokenRepository.deleteAll(tokens);
        }
    } */



}
