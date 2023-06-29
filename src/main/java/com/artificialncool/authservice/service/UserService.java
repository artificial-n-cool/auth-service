package com.artificialncool.authservice.service;


import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.model.Korisnik;
import com.artificialncool.authservice.model.Role;
import com.artificialncool.authservice.model.Smestaj;
import com.artificialncool.authservice.model.enums.StatusRezervacije;
import com.artificialncool.authservice.repository.UserRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    final UserRepository userRepository;

    final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplateBuilder builder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = builder.build();
    }

    public List<Korisnik> getAllKorisnici() {
        return userRepository.findAll();
    }

    public void deleteAllKorisnici() {
        userRepository.deleteAll();
    }

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

    public Korisnik update(String id, UserRequest updated) {
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
        boolean brisi = true;
        try {
            ParameterizedTypeReference<List<Smestaj>> responseType = new ParameterizedTypeReference<List<Smestaj>>() {
            };

            ResponseEntity<List<Smestaj>> response = restTemplate.exchange(
                    "http://localhost:8085/api/host/smestaj/all",
                    HttpMethod.GET,
                    null,
                    responseType
            );

            List<Smestaj> smestaji = response.getBody();


            if (user.getRoles().get(0).getName().equals("ROLE_GUEST")) { // dozvoljeno brisanje samo ukoliko nema rezervacija
                if (smestaji != null && !smestaji.isEmpty()) {
                    for (var smestaj :
                            smestaji) {
                        if (smestaj.getRezervacije() != null && !smestaj.getRezervacije().isEmpty()) {
                            for (var reservation :
                                    smestaj.getRezervacije()) {
                                if (reservation.getKorisnikID().equals(user.getId())
                                        && reservation.getStatusRezervacije().equals(StatusRezervacije.PRIHVACENO)
                                        && reservation.getDatumOd().isAfter(LocalDate.now())) {
                                    brisi = false;
                                }
                            }
                        }
                    }
                }

            } else if (user.getRoles().get(0).getName().equals("ROLE_HOST")) { // Brisanje povezanih smestaja
                if (smestaji != null && !smestaji.isEmpty()) {
                    for (var smestaj :
                            smestaji) {
                        if (smestaj.getVlasnikID().equals(user.getId())) {
                            try {
                                restTemplate.delete("http://localhost:8080/api/host/smestaj/" + smestaj.getId());
                            } catch (RestClientException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }


        } catch (RestClientException ex) {
            ex.printStackTrace();
        }

        if (!brisi) {
            throw new IllegalArgumentException("Korisnik ima prihvacene rezervacije u buducnosti pa ne moze da obrise nalog");
        }

        user.setEnabled(false);
        userRepository.save(user);
        return true;
    }

}
