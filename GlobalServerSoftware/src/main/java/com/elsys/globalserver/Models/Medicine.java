package com.elsys.globalserver.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private boolean needsPrescription;
    @ManyToMany(mappedBy = "medicines", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Prescription> prescriptions = new ArrayList<>();

    public void setMedicine(Medicine medicine){
        this.name = medicine.getName();
        this.needsPrescription = medicine.isNeedsPrescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return needsPrescription == medicine.needsPrescription && name.equals(medicine.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, needsPrescription);
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "name='" + name + '\'' +
                ", needsPrescription=" + needsPrescription +
                '}';
    }
}
