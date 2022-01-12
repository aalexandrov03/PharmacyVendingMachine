package com.elsys.machine.Control.Router;


import com.elsys.machine.DB_Entities.Medicine;

import java.util.HashMap;

public class RoutingMap {
    private HashMap<Integer, Medicine> map = new HashMap<>();

    public void setMap(HashMap<Integer, Medicine> map) {
        this.map = map;
    }

    public void add(Medicine medicine, int position) {
        map.put(position, medicine);
    }

    public int getId(Medicine medicine) {
        for (int id : map.keySet())
            if (map.get(id).equals(medicine))
                return id;

        return -1;
    }

    public void remove(int position) {
        map.remove(position);
    }
}
