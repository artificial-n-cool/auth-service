package com.artificialncool.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetRequest {
    private String userId;
    private String oldPassword;
    private String newPassword;
}

