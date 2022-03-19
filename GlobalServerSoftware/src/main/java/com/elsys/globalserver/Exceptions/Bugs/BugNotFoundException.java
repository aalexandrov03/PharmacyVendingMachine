package com.elsys.globalserver.Exceptions.Bugs;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BugNotFoundException extends Exception{
    @Override
    public String getMessage(){
        return "Bug does not exist!";
    }
}
