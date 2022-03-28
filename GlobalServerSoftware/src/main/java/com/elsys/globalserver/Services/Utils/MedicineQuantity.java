package com.elsys.globalserver.Services.Utils;

import com.elsys.globalserver.Models.Medicine;
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

    public MedicineQuantity(Medicine medicine, int quantity) {
        this.name = medicine.getName();
        this.needsPrescription = medicine.isNeedsPrescription();
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineQuantity that = (MedicineQuantity) o;
        return needsPrescription == that.needsPrescription && quantity == that.quantity && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, needsPrescription, quantity);
    }
}
