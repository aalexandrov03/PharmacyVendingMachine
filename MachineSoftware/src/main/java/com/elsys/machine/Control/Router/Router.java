package com.elsys.machine.Control.Router;

import com.elsys.machine.Control.Utils.Direction;
import com.elsys.machine.Control.Utils.Motor;
import com.elsys.machine.DB_Entities.Medicine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Router {
    private final List<RouteNode> route = new ArrayList<>();
    private final RoutingMap map;
    private final RouterSettings settings;
    private final long STEPS_ROW_CONST;
    private final long STEPS_SLOT_CONST;

    public Router(RouterSettings settings, RoutingMap map) {
        this.settings = settings;
        this.map = map;
        STEPS_ROW_CONST = (long) settings.DIST_ROWS * settings.STEPS_PER_REV / settings.DIST_PER_REV;
        STEPS_SLOT_CONST = (long) settings.DIST_SLOTS * settings.STEPS_PER_REV / settings.DIST_PER_REV;
    }

    public List<RouteNode> createRoute(Map<Medicine, Integer> query) {
        List<Integer> medicines = toIdList(query);

        for (int i = 0; i < medicines.size() - 1; i++)
            travel(medicines.get(i), medicines.get(i + 1));

        return route;
    }

    private List<Integer> toIdList(Map<Medicine, Integer> order) {
        ArrayList<Integer> medicines = new ArrayList<>();
        medicines.add(-1);

        for (Medicine medicine : order.keySet())
            for (int i = 0; i < order.get(medicine); i++)
                medicines.add(map.getId(medicine));

        medicines.add(-1);
        return medicines;
    }

    private void gotoRow(long curr_row, long next_row) {
        long steps = Math.abs(curr_row - next_row) * STEPS_ROW_CONST;

        if (curr_row < next_row)
            route.add(new RouteNode(Motor.Z, steps, Direction.UP));
        else
            route.add(new RouteNode(Motor.Z, steps, Direction.DOWN));
    }

    private int getRow(int id) {
        return id / settings.COLUMNS + 1;
    }

    private int getRowLastId(int row) {
        return row * settings.COLUMNS - 1;
    }

    private void takeMedicine(int id) {
        route.add(new RouteNode(Motor.Z, STEPS_ROW_CONST, Direction.UP));

        route.add(new RouteNode(Motor.X,
                (getRowLastId(getRow(id)) - id) * STEPS_SLOT_CONST, Direction.RIGHT));

        route.add(new RouteNode(Motor.Z, STEPS_ROW_CONST, Direction.DOWN));
    }

    private long calcStepsFromLastID(int id) {
        return ((long) getRow(id) * settings.COLUMNS - 1) - id * STEPS_SLOT_CONST;
    }

    private void travel(int curr_id, int next_id) {
        if (curr_id == -1) {
            route.add(new RouteNode(Motor.Z,
                    10L * settings.STEPS_PER_REV / settings.DIST_PER_REV, Direction.UP));

            if (getRow(next_id) == 1)
                route.add(new RouteNode(Motor.X, (long) next_id * STEPS_SLOT_CONST, Direction.RIGHT));
            else {
                route.add(new RouteNode(Motor.X,
                        (long) (settings.COLUMNS - 1) * STEPS_SLOT_CONST, Direction.RIGHT));
                gotoRow(1, getRow(next_id));
                route.add(new RouteNode(Motor.X, calcStepsFromLastID(next_id), Direction.LEFT));
            }
            takeMedicine(next_id);
        } else if (next_id == -1) {
            gotoRow(getRow(curr_id), 1);
            route.add(new RouteNode(Motor.X,
                    (settings.COLUMNS - 1) * STEPS_SLOT_CONST, Direction.LEFT));
            route.add(new RouteNode(Motor.Z,
                    10L * settings.STEPS_PER_REV / settings.DIST_PER_REV, Direction.DOWN));
        } else {
            if (getRow(curr_id) != getRow(next_id)) {
                gotoRow(getRow(curr_id), getRow(next_id));
            }
            route.add(new RouteNode(Motor.X,
                    (getRowLastId(getRow(next_id)) - next_id) * STEPS_SLOT_CONST, Direction.LEFT));
            takeMedicine(next_id);
        }
    }
}

