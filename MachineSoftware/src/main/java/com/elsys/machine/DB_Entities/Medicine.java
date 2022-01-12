package com.elsys.machine.DB_Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private boolean needsPrescription;

    public Medicine(String name, double price, int amount, boolean needsPrescription) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.needsPrescription = needsPrescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return Double.compare(medicine.price, price) == 0 && needsPrescription == medicine.needsPrescription && name.equals(medicine.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, needsPrescription);
    }
}
