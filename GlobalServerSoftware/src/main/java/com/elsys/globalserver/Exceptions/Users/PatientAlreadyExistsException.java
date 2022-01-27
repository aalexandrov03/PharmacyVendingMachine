package com.elsys.globalserver.Exceptions.Users;

public class PatientAlreadyExistsException extends Exception{
    private String message;

    public PatientAlreadyExistsException byUsername(String username){
        message = "Patient with username: " + username + " already exists!";
        return this;
    }

    public PatientAlreadyExistsException byID(int id){
        message = "Patient with ID: " + id + " already exists!";
        return this;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
