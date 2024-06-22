package com.toyota.cashier.Config;

import com.toyota.cashier.DAO.TokenRepository;
import com.toyota.cashier.Domain.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;


// to implement the logout , I first need to import the LogoutHandler package

@Component
public class CustomLogOutHandler implements LogoutHandler {
    private TokenRepository tokenRepository;

    // injecting the tokenRepository in the constructor
    public CustomLogOutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    @Override

    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        // making sure we are in the Authorization header
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        // storing the token in the token variable after we make sure it starts after 7 indexes

        String token = authHeader.substring(7);
        // in the storedToken variable we store the token that was found by the tokenRepository
        // which is the token we defined above
        Token storedToken = tokenRepository.findByToken(token).orElse(null);
        if(token != null){
            storedToken.setLoggedOut(true);
           tokenRepository.save(storedToken);
           // each time the user loges out the setLoggedOut will be set to true and the token will be saved
        }


    }
}
