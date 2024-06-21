package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.TokenRepository;
import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Domain.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JwtServiceTest {

    @MockBean
    private TokenRepository tokenRepository;

    @InjectMocks
    private JwtService jwtService;

    private String SECRET_KEY = "4bd7f9e2472cfea005e725097c40e6cfa37868101cafd839cdadd9025bbabdbf";
    private SecretKey signInKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService(tokenRepository);
        signInKey = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64URL.decode(SECRET_KEY));
    }

    @Test
    void extractUsername() {
        // Given
        Roles roles = new Roles();
        roles.setUsername("testUser");
        String token = jwtService.generateToken(roles);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertThat(username).isEqualTo("testUser");
    }

    @Test
    void generateToken() {
        // Given
        Roles roles = new Roles();
        roles.setUsername("testUser");

        // When
        String token = jwtService.generateToken(roles);

        // Then
        Claims claims = Jwts.parser().verifyWith(signInKey).build().parseClaimsJws(token).getPayload();
        assertThat(claims.getSubject()).isEqualTo("testUser");
    }

    @Test
    void isValid_ValidToken() {
        // Given
        Roles roles = new Roles();
        roles.setUsername("testUser");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(roles);

        Token savedToken = new Token();
        savedToken.setToken(token);
        savedToken.setLoggedOut(false);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(savedToken));

        // When
        boolean isValid = jwtService.isValid(token, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void isValid_InvalidToken() {
        // Given
        Roles roles = new Roles();
        roles.setUsername("testUser");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(roles);

        Token savedToken = new Token();
        savedToken.setToken(token);
        savedToken.setLoggedOut(true);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(savedToken));

        // When
        boolean isValid = jwtService.isValid(token, userDetails);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void isTokenExpired_NotExpiredToken() {
        // Given
        Roles roles = new Roles();
        roles.setUsername("testUser");
        String token = jwtService.generateToken(roles);

        // When
        boolean isExpired = jwtService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    void isTokenExpired_ExpiredToken() {
        // Given
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)) // 2 days ago
                .setExpiration(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000)) // 1 day ago
                .signWith(signInKey)
                .compact();

        // When & Then
        assertThatThrownBy(() -> jwtService.isTokenExpired(expiredToken))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
    }
}
