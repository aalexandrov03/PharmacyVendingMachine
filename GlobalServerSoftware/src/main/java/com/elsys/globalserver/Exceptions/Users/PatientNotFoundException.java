package com.elsys.globalserver.Exceptions.Users;

public class PatientNotFoundException extends Exception{
    private String message;

    public PatientNotFoundException byUsername(String username){
        message = "Patient with username: " + username + " does not exist!";
        return this;
    }

    public PatientNotFoundException byID(int id){
        message = "Patient with ID: " + id + " does not exist!";
        return this;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
