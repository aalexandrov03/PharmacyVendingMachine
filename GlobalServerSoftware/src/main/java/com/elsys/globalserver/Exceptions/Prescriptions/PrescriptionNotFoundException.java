package com.elsys.globalserver.Exceptions.Prescriptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PrescriptionNotFoundException extends Exception{
    @Override
    public String getMessage(){
        return "Prescription does not exist!";
    }
}
