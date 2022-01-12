package com.elsys.machine.DB_Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Tests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String testDate;

    @Column(nullable = false)
    private boolean testPassed;

    public Tests(String testDate, boolean testPassed) {
        this.testDate = testDate;
        this.testPassed = testPassed;
    }
}
