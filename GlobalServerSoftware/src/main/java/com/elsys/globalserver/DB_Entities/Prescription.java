package com.elsys.globalserver.DB_Entities;

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
    private int id;
    @Column(nullable = false)
    private boolean valid;
    @Column(nullable = false)
    private boolean executed;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Patient user;
    @ManyToMany
    @JoinTable(
            name = "medicines_prescriptions",
            joinColumns = @JoinColumn(name = "medicine_id"),
            inverseJoinColumns = @JoinColumn(name = "prescription_id")
    )
    private List<Medicine> medicines = new ArrayList<>();

    public Prescription(){
        this.valid = true;
        this.executed = false;
    }

    public Prescription(Doctor doctor, Patient user) {
        this.doctor = doctor;
        this.user = user;
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }
}
