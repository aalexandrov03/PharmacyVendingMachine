package com.elsys.globalserver.Exceptions.Bugs;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BugNotFoundException extends Exception{
    private final int id;

    @Override
    public String getMessage(){
        return "Bug with ID: " + id + " does not exist!";
    }
}
