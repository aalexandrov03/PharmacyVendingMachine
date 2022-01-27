package com.elsys.globalserver.Exceptions.Users;

public class DoctorNotFoundException extends Exception{
    private String message;

    public DoctorNotFoundException byID(int id){
        message = "Doctor with ID: " + id + " does not exist!";
        return this;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
