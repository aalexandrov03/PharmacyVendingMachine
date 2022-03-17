package com.elsys.machine.Services.Utils;

import lombok.Getter;

@Getter
public enum ValidationResult {
    OK(0, "Prescription is ok!"),
    SHUTDOWN(-1, "Machine shut down"),
    INVALID(1, "Prescription is invalid"),
    NOT_AVAILABLE(3, "Medicines are not available!");

    private final String message;
    private final int status;

    ValidationResult(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
