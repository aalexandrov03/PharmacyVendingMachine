package com.elsys.globalserver.Exceptions.Medicines;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MedicineNotFoundException extends Exception{
    @Override
    public String getMessage() {
        return "Medicine does not exist!";
    }
}
