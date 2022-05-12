package com.elsys.machine.Services.Utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class MedicineQuantity {
    private String name;
    private boolean needsPrescription;
    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineQuantity that = (MedicineQuantity) o;
        return needsPrescription == that.needsPrescription
                && quantity == that.quantity
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, needsPrescription, quantity);
    }
}