package com.elsys.globalserver.DB_Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private CasualUser user;
    @ManyToMany
    @JoinTable(
            name = "medicines_prescriptions",
            joinColumns = @JoinColumn(name = "medicine_id"),
            inverseJoinColumns = @JoinColumn(name = "prescription_id")
    )
    private List<Medicine> medicines = new ArrayList<>();

    public Prescription(Doctor doctor, CasualUser user) {
        this.doctor = doctor;
        this.user = user;
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }
}
