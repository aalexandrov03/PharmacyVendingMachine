package com.elsys.globalserver.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public Prescription(){
        this.valid = true;
    }

    public Prescription(String doctor, String patient) {
        this.valid = true;
        this.doctor = doctor;
        this.patient = patient;
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }
}
