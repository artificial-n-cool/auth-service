package com.artificialncool.authservice.integration;

import com.github.dockerjava.api.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.artificialncool.authservice.dto.JwtAuthenticationRequest;
import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestAuthentication extends AbstractIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanForTest() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldRegisterAndLoginGuest() throws Exception {
        String NEW_USERNAME = "new-user";
        String NEW_EMAIL    = "furtula@example.rs";
        String NEW_PASSWORD = "new-password";

        UserRequest newUser = UserRequest.builder()
                                .username(NEW_USERNAME)
                                .password(NEW_PASSWORD)
                                .ime("novak")
                                .prezime("korisnic")
                                .prebivaliste("visegrad")
                                .email(NEW_EMAIL)
                                .build();

        mockMvc.perform(post("/api/auth/signup-guest")
        .content(new ObjectMapper().writeValueAsString(newUser))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").value(NEW_USERNAME))
            .andExpect(jsonPath("$.email").value(NEW_EMAIL))
            .andExpect(jsonPath("$.roles").isArray())
            .andExpect(jsonPath("$.roles.length()").value(1))
            .andExpect(jsonPath("$.roles[0].name").value("ROLE_GUEST"));

        
        JwtAuthenticationRequest loginRequest = new JwtAuthenticationRequest(
            NEW_USERNAME,
            NEW_PASSWORD
        );

        mockMvc.perform(post("/api/auth/login")
        .content(new ObjectMapper().writeValueAsString(loginRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
            //.andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    public void shouldReturnUnauthorized() throws Exception {
        String NEW_USERNAME = "new-user";
        String NEW_EMAIL    = "furtula@example.rs";
        String NEW_PASSWORD = "new-password";

        UserRequest newUser = UserRequest.builder()
                .username(NEW_USERNAME)
                .password(NEW_PASSWORD)
                .ime("novak")
                .prezime("korisnic")
                .prebivaliste("visegrad")
                .email(NEW_EMAIL)
                .build();

        mockMvc.perform(post("/api/auth/signup-guest")
                .content(new ObjectMapper().writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        JwtAuthenticationRequest loginRequest = new JwtAuthenticationRequest(
                NEW_USERNAME,
                NEW_PASSWORD + "!"
        );


        mockMvc.perform(post("/api/auth/login")
                        .content(new ObjectMapper().writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(
                        result.getResolvedException().getMessage().contains("Bad credentials")
                ));
    }

    @Test
    public void shouldSayUserAlreadyExists() throws Exception {
        String NEW_USERNAME = "new-user";
        String NEW_EMAIL    = "furtula@example.rs";
        String NEW_PASSWORD = "new-password";

        UserRequest newUser = UserRequest.builder()
                .username(NEW_USERNAME)
                .password(NEW_PASSWORD)
                .ime("novak")
                .prezime("korisnic")
                .prebivaliste("visegrad")
                .email(NEW_EMAIL)
                .build();

        mockMvc.perform(post("/api/auth/signup-guest")
                .content(new ObjectMapper().writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/auth/signup-guest")
                .content(new ObjectMapper().writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isConflict());
    }
}
