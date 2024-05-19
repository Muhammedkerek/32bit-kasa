package com.toyota.cashier.Config;

import com.toyota.cashier.DAO.TokenRepository;
import com.toyota.cashier.Domain.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogOutHandler implements LogoutHandler {
    private TokenRepository tokenRepository;
    public CustomLogOutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override

    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        String token = authHeader.substring(7);
        Token storedToken = tokenRepository.findByToken(token).orElse(null);
        if(token != null){
            storedToken.setLoggedOut(true);
           tokenRepository.save(storedToken);
        }


    }
}
