package com.elsys.machine.Control.Router;

import com.elsys.machine.Control.Utils.Direction;
import com.elsys.machine.Control.Utils.Motor;
import com.elsys.machine.Control.Utils.RouteNode;
import com.elsys.machine.Models.Configuration;
import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Models.RouterSettings;

import java.util.*;
import java.util.stream.Collectors;

public class Router {
    private final List<RouteNode> route = new ArrayList<>();
    private final RouterSettings settings;
    private final List<Mapping> map;
    private final long STEPS_ROW_CONST;
    private final long STEPS_SLOT_CONST;

    public Router(Configuration configuration) {
        this.settings = configuration.getSettings();
        this.map = configuration.getMapping();
        STEPS_ROW_CONST = (long) settings.getDistRows() * settings.getStepsPerRev() / settings.getDistPerRev();
        STEPS_SLOT_CONST = (long) settings.getDistSlots() * settings.getStepsPerRev() / settings.getDistPerRev();
    }

    public List<RouteNode> createRoute(Set<Medicine> query) {
        List<Long> medicines = toIdList(query);

        for (int i = 0; i < medicines.size() - 1; i++)
            travel(medicines.get(i), medicines.get(i + 1));

        return route.stream()
                .filter(node -> node.getSteps() > 0)
                .collect(Collectors.toList());
    }

    private List<Long> toIdList(Set<Medicine> order) {
        List<Long> medicines = new ArrayList<>();

        for (Medicine medicine : order) {
            Optional<Mapping> m = map.stream()
                    .filter(mapping -> mapping.getMedicineName().equals(medicine.getName()))
                    .findFirst();

            if (m.isEmpty()) {
                medicines.clear();
                break;
            }

            for (int i = 0; i < medicine.getAmount(); i++)
                medicines.add(m.get().getSlotID());
        }

        medicines.add(0, (long) -1);
        medicines.add(medicines.size(), (long) -1);
        return medicines;
    }

    private void gotoRow(long curr_row, long next_row) {
        long steps = Math.abs(curr_row - next_row) * STEPS_ROW_CONST;

        if (curr_row < next_row)
            route.add(new RouteNode(Motor.Z, steps, Direction.UP));
        else
            route.add(new RouteNode(Motor.Z, steps, Direction.DOWN));
    }

    private long getRow(long id) {
        return id / settings.getColumns() + 1;
    }

    private long getRowLastId(long row) {
        return row * settings.getColumns();
    }

    private void takeMedicine(long id) {
        route.add(new RouteNode(Motor.Z, STEPS_ROW_CONST, Direction.UP));

        route.add(new RouteNode(Motor.X,
                (getRowLastId(getRow(id)) - id) * STEPS_SLOT_CONST, Direction.RIGHT));

        route.add(new RouteNode(Motor.Z, STEPS_ROW_CONST, Direction.DOWN));
    }

    private long calcStepsFromLastID(long id) {
        return (getRow(id) * settings.getColumns() - 1) - id * STEPS_SLOT_CONST;
    }

    private void travel(long curr_id, long next_id) {
        if (curr_id == -1) {
            route.add(new RouteNode(Motor.Z,
                    10L * settings.getStepsPerRev() / settings.getDistPerRev(), Direction.UP));

            if (getRow(next_id) == 1)
                route.add(new RouteNode(Motor.X, (next_id - 1) * STEPS_SLOT_CONST, Direction.RIGHT));
            else {
                route.add(new RouteNode(Motor.X,
                        (long) (settings.getColumns() - 1) * STEPS_SLOT_CONST, Direction.RIGHT));
                gotoRow(1, getRow(next_id));
                route.add(new RouteNode(Motor.X, calcStepsFromLastID(next_id), Direction.LEFT));
            }
            takeMedicine(next_id);
        } else if (next_id == -1) {
            gotoRow(getRow(curr_id), 1);
            route.add(new RouteNode(Motor.X,
                    (settings.getColumns() - 1) * STEPS_SLOT_CONST, Direction.LEFT));
            route.add(new RouteNode(Motor.Z,
                    10L * settings.getStepsPerRev() / settings.getDistPerRev(), Direction.DOWN));
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

