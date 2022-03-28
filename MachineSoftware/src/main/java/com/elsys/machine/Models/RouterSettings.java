package com.elsys.machine.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouterSettings that = (RouterSettings) o;
        return rows == that.rows
                && columns == that.columns
                && distSlots == that.distSlots
                && distPerRev == that.distPerRev
                && distRows == that.distRows
                && stepsPerRev == that.stepsPerRev;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, columns, distSlots, distPerRev, distRows, stepsPerRev);
    }
}
