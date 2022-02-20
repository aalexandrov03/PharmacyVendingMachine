package com.elsys.globalserver.Exceptions.Users;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DoctorAlreadyExistsException extends Exception{
    @Override
    public String getMessage(){
        return "Doctor already exists!";
    }
}
