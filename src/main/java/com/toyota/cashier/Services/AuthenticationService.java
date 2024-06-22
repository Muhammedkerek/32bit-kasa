package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.RolesRepository;
import com.toyota.cashier.DAO.TokenRepository;
import com.toyota.cashier.DTO.AuthenticationResponse;
import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Domain.Token;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private PasswordEncoder passwordEncoder;
    private RolesRepository rolesRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private TokenRepository tokenRepository;

    public AuthenticationService(PasswordEncoder passwordEncoder,
                                 RolesRepository rolesRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 TokenRepository tokenRepository) {

        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }


    public AuthenticationResponse register(Roles request) {
        if (rolesRepository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse(null, "User already exist");
        }
        System.out.println("Registering user with first name: " + request.getFirstName() + ", last name: " + request.getLastName());
        Roles roles = new Roles();
        roles.setFirstName(request.getFirstName());
        roles.setLastName(request.getLastName());
        roles.setUsername(request.getUsername());
        roles.setPassword(passwordEncoder.encode((request.getPassword())));
        roles.setRole(request.getRole());
        roles = rolesRepository.save(roles);
        System.out.println("User saved with ID: " + roles.getId() + ", first name: " + roles.getFirstName() + ", last name: " + roles.getLastName());
        String jwt = jwtService.generateToken(roles);
        SaveUserToken(jwt, roles);

        return new AuthenticationResponse(jwt, "User registration was successful");
    }


    public AuthenticationResponse authenticate(Roles request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        Roles roles = rolesRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(roles);
        revokeAllTokenByUser(roles);
        SaveUserToken(token, roles);
        return new AuthenticationResponse(token, "User login was successful");
    }


    private void SaveUserToken(String jwt, Roles roles) {
        Token token = new Token();
        token.setToken(jwt);
        token.setRoles(roles);
        token.setLoggedOut(false);
        tokenRepository.save(token);
    }


    public void revokeAllTokenByUser(Roles roles) {
        List<Token> validateTokenListByUser = tokenRepository.findAllUsersById(roles.getId());
        if (!validateTokenListByUser.isEmpty()) {
            validateTokenListByUser.forEach(t -> {
                t.setLoggedOut(true);
            });
        }
        tokenRepository.saveAll(validateTokenListByUser);
    }


}
