package com.artificialncool.authservice.dto;

import lombok.*;

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
