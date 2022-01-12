package com.elsys.machine.Control.Router;

public class RouterSettings {
    public final int ROWS;
    public final int COLUMNS;
    public final int DIST_SLOTS;
    public final int DIST_PER_REV;
    public final int DIST_ROWS;
    public final int STEPS_PER_REV;

    public RouterSettings(int dist_slots, int dist_per_rev, int dist_rows,
                          int rows, int columns, int steps_per_rev) {
        DIST_SLOTS = dist_slots;
        DIST_PER_REV = dist_per_rev;
        DIST_ROWS = dist_rows;
        ROWS = rows;
        COLUMNS = columns;
        STEPS_PER_REV = steps_per_rev;
    }
}
