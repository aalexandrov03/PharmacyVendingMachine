package com.elsys.machine.Services;

import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.DB_Entities.Reloads;
import com.elsys.machine.DB_Entities.Routing;
import com.elsys.machine.DB_Entities.Status;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Services.Microservices.ReloadService;
import com.elsys.machine.Services.Microservices.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AdminService {
    private final MedicinesRepository medicinesRepository;
    private final ReloadService reloadService;
    private final StatusService statusService;

    @Autowired
    public AdminService(MedicinesRepository medicinesRepository,
                        ReloadService reloadService,
                        StatusService statusService) {
        this.medicinesRepository = medicinesRepository;
        this.reloadService = reloadService;
        this.statusService = statusService;
    }

    public List<Medicine> getNoPrescriptionMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAllByNeedsPrescription(false);
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Medicine> getAllMedicines() {
        return reloadService.getCurrentMedicines();
    }

    public List<Routing> getRouting() {
        return reloadService.getCurrentRouting();
    }

    public void reload(List<Medicine> medicines, List<Routing> routing) {
        reloadService.reloadMachine(medicines, routing);
    }

    public void clearReloadHistory() {
        reloadService.clearReloadHistory();
    }

    public List<Reloads> getReloadHistory() {
        return reloadService.getReloadsHistory();
    }

    public Status getStatus() {
        return statusService.getLastStatus();
    }

    public void setStatus(boolean status) {
        statusService.setStatus(status);
    }

    public List<Status> getStatusHistory() {
        return statusService.getStatusHistory();
    }

    public void clearStatusHistory() {
        statusService.clearStatusHistory();
    }
}
