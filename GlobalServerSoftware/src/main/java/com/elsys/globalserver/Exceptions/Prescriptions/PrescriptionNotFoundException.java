package com.elsys.globalserver.Exceptions.Prescriptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PrescriptionNotFoundException extends Exception{
    private int id;

    @Override
    public String getMessage(){
        return "Prescription with ID: " + id + " does not exist!";
    }
}
