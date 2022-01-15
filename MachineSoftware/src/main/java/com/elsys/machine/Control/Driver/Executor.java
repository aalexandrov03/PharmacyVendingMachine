package com.elsys.machine.Control.Driver;

import com.elsys.machine.Control.Router.RouteNode;
import com.elsys.machine.Control.Utils.Direction;
import com.elsys.machine.Control.Utils.Motor;
import org.python.util.PythonInterpreter;

import java.util.List;

public class Executor {
    private static Executor executor = null;

    private Executor() {
    }

    public static Executor getExecutor() {
        if (executor == null)
            executor = new Executor();

        return executor;
    }

    public void execute(List<RouteNode> route) {
        for (RouteNode node : route) {
            String motor = "Z", dir = "0";

            if (node.dir == Direction.UP || node.dir == Direction.RIGHT)
                dir = "1";

            if (node.id == Motor.X)
                motor = "X";

            String[] args = {"mcontrol.py", "execute", motor, String.valueOf(node.steps), dir};
            PythonInterpreter.initialize(System.getProperties(), System.getProperties(), args);
            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.execfile(getClass().getResourceAsStream("/mcontrol.py"));
            interpreter.close();
        }
    }
}
