package com.elsys.machine.DB_Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Reloads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String reloadDate;

    public Reloads(String reloadDate) {
        this.reloadDate = reloadDate;
    }
}
