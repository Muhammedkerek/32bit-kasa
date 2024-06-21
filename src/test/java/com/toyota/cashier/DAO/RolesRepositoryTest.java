package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.DTO.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.toyota.cashier")
@ActiveProfiles("test")
public class RolesRepositoryTest {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        // Setting up a role in the database for testing
        Roles role = new Roles();
        role.setUsername("testUser");
        role.setRole(Role.CASHIER);
        entityManager.persistAndFlush(role);
    }

    @Test
    public void whenFindByUsername_thenReturnRole() {
        // given
        String username = "testUser";

        // when
        Optional<Roles> found = rolesRepository.findByUsername(username);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(username);
        assertThat(found.get().getRole()).isEqualTo(Role.CASHIER);
    }

    @Test
    public void whenFindByUsernameNotExists_thenReturnEmpty() {
        // when
        Optional<Roles> found = rolesRepository.findByUsername("nonExistingUser");

        // then
        assertThat(found).isNotPresent();
    }
}
