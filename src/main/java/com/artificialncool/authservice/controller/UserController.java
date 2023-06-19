package com.artificialncool.authservice.controller;


import com.artificialncool.authservice.dto.DisableRequest;
import com.artificialncool.authservice.dto.ResetRequest;
import com.artificialncool.authservice.dto.UserRequest;
import com.artificialncool.authservice.model.Korisnik;
import com.artificialncool.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RestTemplateBuilder builder;

    @PutMapping("/manage/reset-pass")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetRequest resetRequest) {
        try {
            userService.resetPassword(
                    resetRequest.getUserId(),
                    resetRequest.getNewPassword(),
                    resetRequest.getOldPassword()
            );

            return ResponseEntity.ok().build();
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", e);
        }
    }

    @PutMapping("/manage/disable-account")
    public ResponseEntity<Void> disableAccount(@RequestBody DisableRequest disableRequest) {
        try {
            userService.disableAccount(disableRequest.getId(), disableRequest.getPassword());
            return ResponseEntity.ok().build();
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Korisnik> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {
        try {
            Korisnik old = userService.findById(id);
            Korisnik updated = userService.update(id, userRequest);
            try {
                builder.build().exchange(
                        String.format("http://notifications-app:8080/api/notifications/changeUsername/%s/%s", old.getUsername(), updated.getUsername()),
                        HttpMethod.PUT,
                        null,
                        Void.class
                );
            }
            catch (RestClientException ex) {
                ex.printStackTrace();
                System.out.println("Nebitno");
            }
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user", e);
        }
    }

}
