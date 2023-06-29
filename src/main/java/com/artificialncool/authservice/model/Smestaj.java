package com.artificialncool.authservice.model;

import com.artificialncool.authservice.model.helpers.Cena;
import com.artificialncool.authservice.model.helpers.OcenaSmestaja;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@Document("smestaji")
public class Smestaj {
    @Id
    private String id;

    private String naziv;
    private String lokacija;
    private String pogodnosti;
    private String opis;
    private Integer minGostiju;
    private Integer maxGostiju;
    private Double prosecnaOcena;
    private Cena baseCena;

    private List<String> slike = new ArrayList<>();
    private List<OcenaSmestaja> ocene = new ArrayList<>();
    private List<Promocija> promocije = new ArrayList<>();
    private List<Rezervacija> rezervacije = new ArrayList<>();

    private String vlasnikID;
}
