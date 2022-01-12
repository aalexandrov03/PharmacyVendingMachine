package com.elsys.machine.Services.AdminService;

import com.elsys.machine.Control.Driver.Executor;
import com.elsys.machine.Control.Router.RouteNode;
import com.elsys.machine.Control.Router.Router;
import com.elsys.machine.Control.Router.RouterSettings;
import com.elsys.machine.Control.Router.RoutingMap;
import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.DB_Entities.Routing;
import com.elsys.machine.DB_Entities.Tests;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.DataAccess.TestsRepository;
import com.elsys.machine.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TestService {
    private final TestsRepository testsRepository;
    private final OrderService orderService;
    private final ReloadService reloadService;
    private final MedicinesRepository medicinesRepository;
    private boolean testActive;

    @Autowired
    public TestService(TestsRepository testsRepository,
                       OrderService orderService,
                       ReloadService reloadService,
                       MedicinesRepository medicinesRepository) {
        this.testsRepository = testsRepository;
        this.orderService = orderService;
        this.reloadService = reloadService;
        this.medicinesRepository = medicinesRepository;
        testActive = false;
    }

    public List<Tests> getTestsHistory() {
        Iterable<Tests> testsHistory = testsRepository.findAll();
        return StreamSupport.stream(testsHistory.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void clearTestsHistory() {
        testsRepository.deleteAll();
    }

    public void executeTestPrescription(List<Medicine> testPrescription) {
        if (!testActive) {
            testActive = true;
            int res = orderService.validatePrescription(testPrescription);

            if (res == 0 || res == 1) {
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
                for (Medicine medicine : testPrescription)
                    if (!order.containsKey(medicine))
                        order.put(medicine, Collections.frequency(testPrescription, medicine));


                Router router = new Router(routerSettings, map);
                List<RouteNode> route = router.createRoute(order);

                try{
                    synchronized(Executor.getExecutor()){
                        Executor.getExecutor().execute(route);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void submitResult(boolean result) {
        if (testActive) {
            testsRepository.save(new Tests(LocalDateTime.now().toString(), result));
            testActive = false;
        }
    }
}
