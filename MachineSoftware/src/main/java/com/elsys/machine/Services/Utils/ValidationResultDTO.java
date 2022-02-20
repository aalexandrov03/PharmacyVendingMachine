package com.elsys.machine.Services.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidationResultDTO {
    private int status;
    private String message;
}
