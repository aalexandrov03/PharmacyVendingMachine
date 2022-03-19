package com.elsys.globalserver.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false, unique = true)
    private Long uin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(fullName, doctor.fullName)
                && Objects.equals(email, doctor.email)
                && Objects.equals(password, doctor.password)
                && Objects.equals(region, doctor.region)
                && Objects.equals(uin, doctor.uin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email, password, region, uin);
    }
}
