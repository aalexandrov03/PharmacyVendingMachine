package com.elsys.globalserver.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private int id;
    @Column(nullable = false)
    private boolean valid;
    @Column(nullable = false)
    private int doctorId;
    @Column(nullable = false)
    private int patientId;

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

    public Prescription(int doctorId, int patientId) {
        this.valid = true;
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }
}
