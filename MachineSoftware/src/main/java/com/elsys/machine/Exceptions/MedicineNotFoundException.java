package com.elsys.machine.Exceptions;

public class MedicineNotFoundException extends RuntimeException {
    private final String name;

    public MedicineNotFoundException(String name){
        this.name = name;
    }

    @Override
    public String getMessage() {
        return  name + " not found!";
    }
}
