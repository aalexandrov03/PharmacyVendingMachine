package com.elsys.machine.Exceptions;

public class MedicineAlreadyExistsException extends RuntimeException {
    private final String name;

    public MedicineAlreadyExistsException(String name){
        this.name = name;
    }

    @Override
    public String getMessage() {
        return name + " already exists!";
    }
}
