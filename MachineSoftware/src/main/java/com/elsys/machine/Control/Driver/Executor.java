package com.elsys.machine.Control.Driver;

import com.elsys.machine.Control.Utils.RouteNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public void execute(List<RouteNode> route) throws IOException {
        List<String> command = new ArrayList<>();
        command.add("python3");
        command.add("/home/sasho/machine/mcontrol.py");

        for (RouteNode node : route) {
            command.add(String.valueOf(node.getId()));
            command.add(String.valueOf(node.getSteps()));
            command.add(String.valueOf(node.getDir()));
        }

        ProcessBuilder executionBuilder =
                new ProcessBuilder(command);
        executionBuilder.redirectErrorStream(true);

        Process executionProcess = executionBuilder.start();

        try {
            executionProcess.waitFor();
        } catch (InterruptedException e) {
            throw new IOException("Something unexpected happened when executing!");
        }
    }
}

