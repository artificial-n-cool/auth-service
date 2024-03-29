package com.artificialncool.authservice.controller;

import com.artificialncool.authservice.dto.JwtAuthenticationRequest;
import com.artificialncool.authservice.dto.LoginUserDTO;
import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.model.Korisnik;
import com.artificialncool.authservice.model.Role;
import com.artificialncool.authservice.security.TokenUtils;
import com.artificialncool.authservice.service.UserService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    public ResponseEntity<LoginUserDTO> createAuthenticationToken(
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
        String jwt = tokenUtils.generateToken(user);
        long expiresIn = tokenUtils.getExpiredIn();

        LoginUserDTO loginUserDTO = LoginUserDTO.builder()
                .id(user.getId())
                .ime(user.getIme())
                .prezime(user.getPrezime())
                .username(user.getUsername())
                .email(user.getEmail())
                .prebivaliste(user.getPrebivaliste())
                .jwt(jwt)
                .accessToken(jwt)
                .authorities(List.of(user.getRoles().get(0).getName()))
                .build();

        // Vrati token kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok(loginUserDTO);
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
