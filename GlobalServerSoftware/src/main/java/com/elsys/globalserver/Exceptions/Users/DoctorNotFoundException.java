package com.elsys.globalserver.Exceptions.Users;

public class DoctorNotFoundException extends Exception{
    @Override
    public String getMessage(){
        return "Doctor does not exist!";
    }
}
