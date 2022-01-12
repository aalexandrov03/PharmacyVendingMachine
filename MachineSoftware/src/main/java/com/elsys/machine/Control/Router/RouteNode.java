package com.elsys.machine.Control.Router;


import com.elsys.machine.Control.Utils.Direction;
import com.elsys.machine.Control.Utils.Motor;

public class RouteNode {
    public Motor id;
    public long steps;
    public Direction dir;

    public RouteNode(Motor id, long steps, Direction dir) {
        this.id = id;
        this.steps = steps;
        this.dir = dir;
    }
}