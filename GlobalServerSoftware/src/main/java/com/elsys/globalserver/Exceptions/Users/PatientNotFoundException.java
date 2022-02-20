package com.elsys.globalserver.Exceptions.Users;

public class PatientNotFoundException extends Exception{
    @Override
    public String getMessage(){
        return "Patient does not exist!";
    }
}
