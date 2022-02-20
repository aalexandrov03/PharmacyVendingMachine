package com.elsys.globalserver.Exceptions.Medicines;

import com.elsys.globalserver.Models.Medicine;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MedicineAlreadyExistsException extends Exception{
    private final Medicine duplicate_medicine;

    @Override
    public String getMessage(){
        return duplicate_medicine + " already exists!";
    }
}
