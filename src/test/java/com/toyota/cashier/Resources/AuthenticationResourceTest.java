package com.toyota.cashier.Resources;

import com.toyota.cashier.DTO.AuthenticationResponse;
import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest

class AuthenticationResourceTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationResource authenticationResource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationResource).build();
    }

    @Test
    void register_ShouldReturnOkStatus() throws Exception {
        Roles roles = new Roles();
        roles.setUsername("testuser");
        roles.setPassword("password");

        AuthenticationResponse authResponse = new AuthenticationResponse("token123", "Success");

        when(authenticationService.register(any(Roles.class))).thenReturn(authResponse);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void login_ShouldReturnOkStatus() throws Exception {
        Roles roles = new Roles();
        roles.setUsername("testuser");
        roles.setPassword("password");

        AuthenticationResponse authResponse = new AuthenticationResponse("token123", "Success");

        when(authenticationService.authenticate(any(Roles.class))).thenReturn(authResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void logout_ShouldReturnOkStatus() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        mockMvc.perform(post("/logout").requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }
}
