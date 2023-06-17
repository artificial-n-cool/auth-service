package com.artificialncool.authservice.controller;


import com.artificialncool.authservice.dto.DisableRequest;
import com.artificialncool.authservice.dto.ResetRequest;
import com.artificialncool.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PutMapping("/reset-pass")
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

    @PutMapping("/disable-account")
    public ResponseEntity<Void> disableAccount(@RequestBody DisableRequest disableRequest) {
        try {
            userService.disableAccount(disableRequest.getId(), disableRequest.getPassword());
            return ResponseEntity.ok().build();
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", e);
        }
    }

}
