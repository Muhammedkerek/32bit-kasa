package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Domain.Token;
import com.toyota.cashier.DTO.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.toyota.cashier")
@ActiveProfiles("test")
public class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Roles role;
    private Token token1;
    private Token token2;
    private Token token3;

    @BeforeEach
    public void setUp() {
        // Setting up a role in the database for testing
        role = new Roles();
        role.setUsername("testUser");
        role.setRole(Role.ADMIN);
        entityManager.persistAndFlush(role);

        // Setting up some tokens in the database for testing
        token1 = new Token();
        token1.setToken("token1");
        token1.setRoles(role);
        token1.setLoggedOut(false);

        token2 = new Token();
        token2.setToken("token2");
        token2.setRoles(role);
        token2.setLoggedOut(true); // This token is logged out

        token3 = new Token();
        token3.setToken("token3");
        token3.setRoles(role);
        token3.setLoggedOut(false);

        entityManager.persistAndFlush(token1);
        entityManager.persistAndFlush(token2);
        entityManager.persistAndFlush(token3);
    }

    @Test
    public void whenFindAllUsersById_thenReturnActiveTokens() {
        // given
        Long userId = role.getId();

        // when
        List<Token> activeTokens = tokenRepository.findAllUsersById(userId);

        // then
        assertThat(activeTokens).isNotNull();
        assertThat(activeTokens.size()).isEqualTo(2); // Check if it returns only active tokens
        assertThat(activeTokens).extracting(Token::getToken).contains("token1", "token3");
    }

    @Test
    public void whenFindByToken_thenReturnToken() {
        // given
        String tokenString = "token1";

        // when
        Optional<Token> foundToken = tokenRepository.findByToken(tokenString);

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo(tokenString);
    }

    @Test
    public void whenFindByTokenNotExists_thenReturnEmpty() {
        // given
        String tokenString = "nonExistingToken";

        // when
        Optional<Token> foundToken = tokenRepository.findByToken(tokenString);

        // then
        assertThat(foundToken).isNotPresent();
    }
}
