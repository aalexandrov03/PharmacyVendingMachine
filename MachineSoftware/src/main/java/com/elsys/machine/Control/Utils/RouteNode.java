package com.elsys.machine.Control.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RouteNode {
    private Motor id;
    private long steps;
    private Direction dir;
}