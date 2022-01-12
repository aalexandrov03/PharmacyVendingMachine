package com.elsys.machine.DB_Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private boolean isActive;

    public Status(String date, boolean isActive) {
        this.date = date;
        this.isActive = isActive;
    }
}
