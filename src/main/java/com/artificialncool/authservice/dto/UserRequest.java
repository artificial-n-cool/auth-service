package com.artificialncool.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private String username;

    private String password;

    private String ime;

    private String prezime;
    
    private String email;

    private String prebivaliste;
}
