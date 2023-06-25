package com.artificialncool.authservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserDTO {
    private String id;
    private String ime;
    private String username;
    private String prezime;
    private String email;
    private String prebivaliste;
    private String jwt;
    private List<String> authorities;
    private Long expiresIn;
    private String accessToken;
}
