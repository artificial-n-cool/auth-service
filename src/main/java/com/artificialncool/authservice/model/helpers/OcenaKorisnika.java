package com.artificialncool.authservice.model.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class OcenaKorisnika {
    @Id
    private String id;

    private Double ocena;
    private LocalDateTime datum;

    private String ocenjivacID;
}
