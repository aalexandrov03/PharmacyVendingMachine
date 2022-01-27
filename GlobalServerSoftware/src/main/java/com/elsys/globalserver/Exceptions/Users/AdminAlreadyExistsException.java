package com.elsys.globalserver.Exceptions.Users;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdminAlreadyExistsException extends Exception{
    @Override
    public String getMessage() {
        return "Admin account already exists!";
    }
}
