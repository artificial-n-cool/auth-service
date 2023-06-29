package com.artificialncool.authservice.model.helpers;

import com.artificialncool.authservice.model.enums.TipCene;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cena {
    private String id;
    private Double cena;
    private TipCene tipCene;
}
