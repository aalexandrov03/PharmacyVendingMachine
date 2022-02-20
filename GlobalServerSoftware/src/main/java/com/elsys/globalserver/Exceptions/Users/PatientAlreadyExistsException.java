package com.elsys.globalserver.Exceptions.Users;

public class PatientAlreadyExistsException extends Exception{
    @Override
    public String getMessage() {
        return "Patient already exists!";
    }
}
