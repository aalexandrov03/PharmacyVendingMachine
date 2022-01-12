package com.elsys.machine.Services;

import com.elsys.machine.Control.Driver.Executor;
import com.elsys.machine.Control.Router.RouteNode;
import com.elsys.machine.Control.Router.Router;
import com.elsys.machine.Control.Router.RouterSettings;
import com.elsys.machine.Control.Router.RoutingMap;
import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.DB_Entities.Routing;
import com.elsys.machine.DB_Entities.Status;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.DataAccess.StatusRepository;
import com.elsys.machine.Services.AdminService.ReloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {
    private final MedicinesRepository medicinesRepository;
    private final StatusRepository statusRepository;
    private final ReloadService reloadService;

    @Autowired
    public OrderService(MedicinesRepository medicinesRepository,
                        StatusRepository statusRepository,
                        ReloadService reloadService) {
        this.medicinesRepository = medicinesRepository;
        this.statusRepository = statusRepository;
        this.reloadService = reloadService;
    }

    public List<Medicine> getNoPrescriptionMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAllByNeedsPrescription(false);
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Boolean getStatus() {
        Optional<Status> lastStatus = statusRepository.findTopByOrderByIdDesc();

        if (lastStatus.isEmpty())
            return null;

        return lastStatus.get().isActive();
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

        RouterSettings routerSettings = new RouterSettings(
                55, 100, 70, 2, 7, 6300
        );

        RoutingMap map = new RoutingMap();

        List<Routing> routing = reloadService.getCurrentRouting();
        for(Routing record : routing){
            Optional<Medicine> medicine = medicinesRepository.findMedicineByName(record.getMedicine_name());
            map.add(medicine.get(), record.getMedicine_id());
        }

        Map<Medicine, Integer> order = new HashMap<>();
        for (Medicine medicine : prescription)
            if (!order.containsKey(medicine))
                order.put(medicine, Collections.frequency(prescription, medicine));


        Router router = new Router(routerSettings, map);
        List<RouteNode> route = router.createRoute(order);

        try{
            synchronized(Executor.getExecutor()){
                Executor.getExecutor().execute(route);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Medicine getMedByObject(List<Medicine> medicines, Medicine medicine){
        for (Medicine m : medicines){
            if (m.equals(medicine))
                return m;
        }
        return null;
    }
}