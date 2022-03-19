package com.elsys.globalserver.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private boolean valid;
    @Column(nullable = false)
    private String doctor;
    @Column(nullable = false)
    private String patient;

    @ManyToMany
    @JoinTable(
            name = "medicines_prescriptions",
            joinColumns = @JoinColumn(name = "medicine_id"),
            inverseJoinColumns = @JoinColumn(name = "prescription_id")
    )
    private List<Medicine> medicines = new ArrayList<>();

    public Prescription() {
        this.valid = true;
    }

    public Prescription(String doctor, String patient) {
        this.valid = true;
        this.doctor = doctor;
        this.patient = patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return valid == that.valid
                && Objects.equals(doctor, that.doctor)
                && Objects.equals(patient, that.patient)
                && medicines.containsAll(that.medicines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, doctor, patient, medicines);
    }
}
