package com.artificialncool.authservice.model;

import com.artificialncool.authservice.model.enums.StatusRezervacije;
import lombok.*;

import javax.persistence.Id;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Rezervacija {
    @Id
    private String id;

    private Integer brojOsoba;
    private LocalDate datumOd;
    private LocalDate datumDo;
    private StatusRezervacije statusRezervacije;
    private String korisnikID;
}
