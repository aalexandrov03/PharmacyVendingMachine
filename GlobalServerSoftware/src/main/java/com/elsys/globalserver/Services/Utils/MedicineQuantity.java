package com.elsys.globalserver.Services.Utils;

import com.elsys.globalserver.Models.Medicine;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineQuantity {
    private String name;
    private double price;
    private boolean needsPrescription;
    private int quantity;

    public MedicineQuantity(Medicine medicine, int quantity) {
        this.name = medicine.getName();
        this.price = medicine.getPrice();
        this.needsPrescription = medicine.isNeedsPrescription();
        this.quantity = quantity;
    }
}
