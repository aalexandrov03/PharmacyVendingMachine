package com.elsys.globalserver.Exceptions.Medicines;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MedicineAlreadyExistsException extends Exception{
    @Override
    public String getMessage(){
        return "Medicine already exists!";
    }
}
