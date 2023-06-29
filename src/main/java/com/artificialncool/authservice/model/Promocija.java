package com.artificialncool.authservice.model;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Promocija {
    private String id;
    private LocalDate datumOd;
    private LocalDate datumDo;
    private Double procenat;
    private List<DayOfWeek> dani;
}
