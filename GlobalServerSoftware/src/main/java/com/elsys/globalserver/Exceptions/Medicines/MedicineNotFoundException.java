package com.elsys.globalserver.Exceptions.Medicines;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MedicineNotFoundException extends Exception{
    private int medicine_id;

    @Override
    public String getMessage() {
        return "Medicine with ID: " + medicine_id + " does not exist!";
    }
}
