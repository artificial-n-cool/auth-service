package com.artificialncool.authservice.integration;

import com.artificialncool.authservice.dto.JwtAuthenticationRequest;
import com.artificialncool.authservice.dto.ResetRequest;
import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.dto.UserTokenState;
import com.artificialncool.authservice.model.Korisnik;
import com.artificialncool.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestAccountManage extends AbstractIntegrationTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Autowired
    TestRestTemplate dispatcher;

    @Autowired
    UserRepository userRepository;

    public String signup() {
        userRepository.deleteAll();

        UserRequest newUser = UserRequest.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .ime("novak")
                .prezime("korisnic")
                .prebivaliste("visegrad")
                .email("furtula@example.rs")
                .build();

        ResponseEntity<Korisnik> responseEntity
                = dispatcher.postForEntity(
                "/api/auth/signup-guest",
                newUser,
                Korisnik.class
        );

        return responseEntity.getBody().getId();
    }

    public HttpHeaders login() {
        ResponseEntity<UserTokenState> responseEntity
                = dispatcher.postForEntity(
                "/api/auth/login",
                new JwtAuthenticationRequest(USERNAME, PASSWORD),
                UserTokenState.class
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + responseEntity.getBody().getAccessToken());
        return headers;
    }

    @BeforeEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldChangeUsername() {
        String id = signup();
        var headers = login();

        UserRequest updatedUserRequest = UserRequest.builder()
                .username(USERNAME + "!")
                .password(PASSWORD)
                .ime("novak")
                .prezime("korisnic")
                .prebivaliste("visegrad")
                .email("furtula@example.rs")
                .build();

        ResponseEntity<Korisnik> updateUserResponse
                = dispatcher.exchange(
                    "/api/user/" + id,
                        HttpMethod.PUT,
                        new HttpEntity<>(updatedUserRequest, headers),
                        Korisnik.class
        );

        Korisnik updatedUser = updateUserResponse.getBody();
        assertNotNull(updatedUser);
        assertEquals(updateUserResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(USERNAME + "!", updatedUser.getUsername());
    }

    @Test
    public void shouldChangePassword() {
        String id = signup();
        var headers = login();

        ResetRequest resetRequest = new ResetRequest(
                id,
                PASSWORD,
                PASSWORD + "!"
        );

        ResponseEntity<Void> updateUserResponse
                = dispatcher.exchange(
                    "/api/user/manage/reset-pass",
                    HttpMethod.PUT,
                    new HttpEntity<>(resetRequest, headers),
                    Void.class
        );

        assertEquals(HttpStatus.OK, updateUserResponse.getStatusCode());
        ResponseEntity<UserTokenState> loginWithNewPasswordRequest
                = dispatcher.postForEntity(
                "/api/auth/login",
                new JwtAuthenticationRequest(USERNAME, PASSWORD + "!"),
                UserTokenState.class
        );

        assertEquals(HttpStatus.OK, loginWithNewPasswordRequest.getStatusCode());
        assertNotNull(loginWithNewPasswordRequest.getBody());
    }

    @Test
    public void shouldRejectPasswordChange() {
        String id = signup();
        var headers = login();

        ResetRequest resetRequest = new ResetRequest(
                id,
                PASSWORD + "!",
                PASSWORD + "!"
        );

        ResponseEntity<Void> updateUserResponse
                = dispatcher.exchange(
                "/api/user/manage/reset-pass",
                HttpMethod.PUT,
                new HttpEntity<>(resetRequest, headers),
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, updateUserResponse.getStatusCode());
    }
}
