package com.elsys.machine.Services.AdminService;

import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.DB_Entities.Reloads;
import com.elsys.machine.DB_Entities.Routing;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.DataAccess.ReloadsRepository;
import com.elsys.machine.DataAccess.RoutingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReloadService {
    private final MedicinesRepository medicinesRepository;
    private final ReloadsRepository reloadsRepository;
    private final RoutingRepository routingRepository;

    @Autowired
    public ReloadService(MedicinesRepository medicinesRepository,
                         ReloadsRepository reloadsRepository,
                         RoutingRepository routingRepository) {
        this.medicinesRepository = medicinesRepository;
        this.reloadsRepository = reloadsRepository;
        this.routingRepository = routingRepository;
    }

    public List<Medicine> getCurrentMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAll();
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Routing> getCurrentRouting() {
        Iterable<Routing> routing = routingRepository.findAll();
        return StreamSupport.stream(routing.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void reloadMachine(List<Medicine> medicines, List<Routing> routing) {
        routingRepository.deleteAll();
        medicinesRepository.deleteAll();

        routingRepository.saveAll(routing);
        medicinesRepository.saveAll(medicines);

        reloadsRepository.save(new Reloads(LocalDateTime.now().toString()));
    }

    public void clearReloadHistory() {
        reloadsRepository.deleteAll();
    }

    public List<Reloads> getReloadsHistory() {
        Iterable<Reloads> reloads_history = reloadsRepository.findAll();
        return StreamSupport.stream(reloads_history.spliterator(), false)
                .collect(Collectors.toList());
    }
}
