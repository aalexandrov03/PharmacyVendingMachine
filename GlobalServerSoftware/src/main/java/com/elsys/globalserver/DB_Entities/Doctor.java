package com.elsys.globalserver.DB_Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String workplace;
    @OneToMany(mappedBy = "doctor")
    @JsonIgnore
    private Set<Prescription> prescriptions = new HashSet<>();

    public Doctor(String fullName, String username, String password, String workplace) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.workplace = workplace;
    }
}
