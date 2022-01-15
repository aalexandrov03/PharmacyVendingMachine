package com.elsys.machine.Services.Microservices;

import com.elsys.machine.Control.Driver.Executor;
import com.elsys.machine.Control.Router.RouteNode;
import com.elsys.machine.Control.Router.Router;
import com.elsys.machine.Control.Router.RouterSettings;
import com.elsys.machine.Control.Router.RoutingMap;
import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.DB_Entities.Routing;
import com.elsys.machine.DataAccess.MedicinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExecutionService {
    private final RouterSettings routerSettings;
    private final MedicinesRepository medicinesRepository;
    private final ReloadService reloadService;

    @Autowired
    public ExecutionService(MedicinesRepository medicinesRepository,
                            ReloadService reloadService) {
        routerSettings = new RouterSettings(
                55, 100, 70, 2, 7, 6300
        );
        this.medicinesRepository = medicinesRepository;
        this.reloadService = reloadService;
    }

    public int validatePrescription(List<Medicine> prescription) {
        Iterable<Medicine> medicines = medicinesRepository.findAll();

        int available = (int) prescription.stream().filter(medicine -> {
            boolean flag = false;
            for (Medicine m : medicines)
                if (medicine.equals(m)) {
                    if (medicine.getAmount() <= m.getAmount()) {
                        flag = true;
                        break;
                    }
                }
            return flag;
        }).count();

        if (available == 0)
            return -1;
        else if (available == prescription.size())
            return 1;
        return 0;
    }

    public boolean executePrescription(List<Medicine> prescription) {
        int res = validatePrescription(prescription);

        if (res == -1)
            return false;

        RoutingMap map = new RoutingMap();

        List<Routing> routing = reloadService.getCurrentRouting();
        for (Routing record : routing) {
            Optional<Medicine> medicine = medicinesRepository.findMedicineByName(record.getMedicine_name());
            if (medicine.isEmpty())
                return false;
            map.add(medicine.get(), record.getMedicine_id());
        }

        Map<Medicine, Integer> order = new HashMap<>();
        for (Medicine medicine : prescription)
            if (!order.containsKey(medicine))
                order.put(medicine, Collections.frequency(prescription, medicine));

        Router router = new Router(routerSettings, map);
        List<RouteNode> route = router.createRoute(order);

        try {
            synchronized (Executor.getExecutor()) {
                Executor.getExecutor().execute(route);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //TODO: remove bought medicines amount from database
        return true;
    }
}
