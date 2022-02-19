package com.elsys.machine.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RouterSettings {
    private int rows;
    private int columns;
    private int distSlots;
    private int distPerRev;
    private int distRows;
    private int stepsPerRev;
}
