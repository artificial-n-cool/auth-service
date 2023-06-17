package com.artificialncool.authservice.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.artificialncool.authservice.dto.JwtAuthenticationRequest;
import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.dto.UserTokenState;
import com.artificialncool.authservice.model.Korisnik;
import com.artificialncool.authservice.model.Role;
import com.artificialncool.authservice.security.TokenUtils;
import com.artificialncool.authservice.service.UserService;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {
    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {

        // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
        // AuthenticationException
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", e);
        }

        // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
        // kontekst
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Kreiraj token za tog korisnika
        Korisnik user = (Korisnik) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername());
        long expiresIn = tokenUtils.getExpiredIn();

        // Vrati token kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }

    // Endpoint za registraciju novog korisnika
    @PostMapping("/signup-guest")
    public ResponseEntity<Korisnik> addGuest(@RequestBody UserRequest userRequest, UriComponentsBuilder ucBuilder) {

        Korisnik existUser = (Korisnik) this.userService.findByUsername(userRequest.getUsername());

        if (existUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        Korisnik user = this.userService.save(userRequest, new Role("ROLE_GUEST"));

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/signup-host")
    public ResponseEntity<Korisnik> addHost(@RequestBody UserRequest userRequest, UriComponentsBuilder ucBuilder) {

        Korisnik existUser = (Korisnik) this.userService.findByUsername(userRequest.getUsername());

        if (existUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        Korisnik user = this.userService.save(userRequest, new Role("ROLE_HOST"));

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    
}
