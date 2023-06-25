package com.artificialncool.authservice.service;

import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.model.Role;
import com.artificialncool.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner implements CommandLineRunner {

    @Autowired
    UserService userService;

    @Override
    public void run(String... args) throws Exception {
        UserRequest userRequestHost = UserRequest.builder()
                .username("jokic15")
                .password("pass")
                .ime("Nikola")
                .prezime("Jokic")
                .email("nikola@gmail.com")
                .prebivaliste("Sombor")
                .build();

        Role hostRole = new Role("ROLE_HOST");

        try {
            userService.save(userRequestHost, hostRole);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        UserRequest userRequestGuest = UserRequest.builder()
                .username("kobe")
                .password("pass")
                .ime("Kobe")
                .prezime("Bryant")
                .email("kobe@gmail.com")
                .prebivaliste("LA")
                .build();

        Role guestRole = new Role("ROLE_GUEST");

        try {
            userService.save(userRequestGuest, guestRole);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("GOTOVO SA DB INIT");

    }
}
