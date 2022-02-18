package com.elsys.machine.Services.Utils;

public enum VALIDATION_STATUS {
    ALL_AVAILABLE("ALL AVAILABLE"),
    PARTLY_AVAILABLE("PARTLY AVAILABLE"),
    NOT_AVAILABLE("NOT AVAILABLE"),
    INVALID("INVALID");

    private final String name;

    VALIDATION_STATUS(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }
}
