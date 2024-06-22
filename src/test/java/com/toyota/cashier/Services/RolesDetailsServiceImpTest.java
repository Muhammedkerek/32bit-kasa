package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.RolesRepository;
import com.toyota.cashier.Domain.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolesDetailsServiceImpTest {

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    private RolesDetailsServiceImp rolesDetailsServiceImp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        System.out.println("Started testing the loadUserByUsername function");
        String username = "admin";
        Roles roles = new Roles();
        roles.setUsername(username);

        when(rolesRepository.findByUsername(username)).thenReturn(Optional.of(roles));

        UserDetails userDetails = rolesDetailsServiceImp.loadUserByUsername(username);

        System.out.println("Finished testing the loadUserByUsername function");

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());

    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        String username = "nonexistent";
        System.out.println("Started testing UserDoesNotExist");
        when(rolesRepository.findByUsername(username)).thenReturn(Optional.empty());

        System.out.println("Finished testing UserDoesNotExist");

        assertThrows(UsernameNotFoundException.class, () -> {
            rolesDetailsServiceImp.loadUserByUsername(username);


        });
    }
}
