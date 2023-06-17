package com.artificialncool.authservice.service;


import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.model.Korisnik;
import com.artificialncool.authservice.model.Role;
import com.artificialncool.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    final UserRepository userRepository;

    final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    public Korisnik findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Korisnik findById(String id) throws AccessDeniedException {
        return userRepository.findById(id).orElseThrow(() -> new AccessDeniedException("No no no"));
    }

    public Korisnik update(String id, Korisnik updated) {
        Korisnik old = findById(id);

        old.setUsername(updated.getUsername());
        old.setEmail(updated.getEmail());
        old.setIme(updated.getIme());
        old.setPrezime(updated.getPrezime());
        old.setPrebivaliste(updated.getPrebivaliste());

        return userRepository.save(old);
    }

    public Korisnik save(UserRequest userRequest, Role role) {
        Korisnik u = new Korisnik();
        u.setUsername(userRequest.getUsername());

        // pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
        // treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
        u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        u.setIme(userRequest.getIme());
        u.setPrezime(userRequest.getPrezime());
        u.setEmail(userRequest.getEmail());
        u.setPrebivaliste(userRequest.getPrebivaliste());
        u.setEnabled(true);

        List<Role> roles;
        if (role.getName().equals("ROLE_GUEST"))
             roles = List.of(new Role("ROLE_GUEST"));
        else if (role.getName().equals("ROLE_HOST"))
             roles = List.of(new Role("ROLE_HOST"));
        else
            throw new IllegalArgumentException("No such role");

        u.setRoles(roles);

        return this.userRepository.save(u);
    }

    public boolean resetPassword(String id, String newPassword, String oldPassword) {
        Korisnik user = userRepository.findById(id).orElseThrow(() -> new AccessDeniedException("ooo no no no"));


        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new IllegalArgumentException("Old and new passwords don't match");

        String newPasswordHash = passwordEncoder.encode(newPassword);

        user.setPassword(newPasswordHash);
        user.setLastPasswordResetDate((new Date()).getTime());
        userRepository.save(user);
        return true;
    }

    public boolean disableAccount(String id, String password) {
        Korisnik user = userRepository.findById(id).orElseThrow(() -> new AccessDeniedException("ooo no no no"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new IllegalArgumentException("Old and new passwords don't match");

        user.setEnabled(false);
        userRepository.save(user);
        return true;
    }

}
