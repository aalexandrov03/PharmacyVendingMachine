package com.elsys.machine.Control.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class RouteNode {
    private Motor id;
    private long steps;
    private Direction dir;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteNode routeNode = (RouteNode) o;
        return steps == routeNode.steps && id == routeNode.id && dir == routeNode.dir;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, steps, dir);
    }

    @Override
    public String toString() {
        return id.name() + " " + steps + " " + dir.name();
    }
}