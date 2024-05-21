package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.TokenRepository;
import com.toyota.cashier.Domain.Roles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    // secret key to generate the token
    private String SECRET_KEY = "4bd7f9e2472cfea005e725097c40e6cfa37868101cafd839cdadd9025bbabdbf";
    private TokenRepository tokenRepository;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // This method extracts the username from the JWT token's subject claim.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(Roles roles) {
        String token = Jwts.builder()
                .subject(roles.getUsername())
                .issuedAt(new Date(System.currentTimeMillis())) // the time that the token is issued
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // the time that the token is expired
                .signWith(getSignInKey())
                .compact();
        return token;
    }

    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        boolean isValidToken = tokenRepository.findByToken(token)
                .map(t-> !t.isLoggedOut()).orElse(false);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && isValidToken;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }





}
