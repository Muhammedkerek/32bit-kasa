package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.RolesRepository;
import com.toyota.cashier.DAO.TokenRepository;
import com.toyota.cashier.DTO.AuthenticationResponse;
import com.toyota.cashier.DTO.Role;
import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Domain.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthenticationServiceTest {

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RolesRepository rolesRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_UserAlreadyExists() {
        // Given
        Roles request = new Roles();
        request.setUsername("existingUser");
        when(rolesRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new Roles()));

        // When
        AuthenticationResponse response = authenticationService.register(request);

        // Then
        assertThat(response.getMessage()).isEqualTo("User already exist");
    }

    @Test
    void register_NewUser() {
        // Given
        Roles request = new Roles();
        request.setUsername("newUser");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password");
        request.setRole(Role.STORE_MANAGER);

        when(rolesRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(rolesRepository.save(any(Roles.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(Roles.class))).thenReturn("jwtToken");

        // When
        AuthenticationResponse response = authenticationService.register(request);

        // Then
        assertThat(response.getMessage()).isEqualTo("User registration was successful");
        assertThat(response.getToken()).isEqualTo("jwtToken");

        verify(rolesRepository, times(1)).save(any(Roles.class));
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void authenticate_Success() {
        // Given
        Roles request = new Roles();
        request.setUsername("testUser");
        request.setPassword("password");

        Roles roles = new Roles();
        roles.setUsername("testUser");
        roles.setPassword("encodedPassword");

        when(rolesRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(roles));
        when(jwtService.generateToken(any(Roles.class))).thenReturn("jwtToken");

        // When
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Then
        assertThat(response.getMessage()).isEqualTo("User login was successful");
        assertThat(response.getToken()).isEqualTo("jwtToken");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void revokeAllTokenByUser() {
        // Given
        Roles roles = new Roles();
        roles.setId(1L);

        Token token1 = new Token();
        token1.setToken("token1");
        token1.setLoggedOut(false);
        token1.setRoles(roles);

        Token token2 = new Token();
        token2.setToken("token2");
        token2.setLoggedOut(false);
        token2.setRoles(roles);

        when(tokenRepository.findAllUsersById(roles.getId())).thenReturn(List.of(token1, token2));

        // When
        authenticationService.revokeAllTokenByUser(roles);

        // Then
        assertThat(token1.isLoggedOut()).isTrue();
        assertThat(token2.isLoggedOut()).isTrue();
        verify(tokenRepository, times(1)).saveAll(anyList());
    }
}
